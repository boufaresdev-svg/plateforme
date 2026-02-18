import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User, LoginRequest, LoginResponse } from '../models/user.model';
import { environment } from '../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private readonly TOKEN_KEY = 'sms2i_token';
  private readonly REFRESH_TOKEN_KEY = 'sms2i_refresh_token';
  private readonly USER_KEY = 'sms2i_user';

  constructor(private http: HttpClient) {
    // Vérifier si l'utilisateur est déjà connecté au chargement
    this.initializeAuth();
  }

  private initializeAuth(): void {
    const savedUser = localStorage.getItem(this.USER_KEY);
    const token = localStorage.getItem(this.TOKEN_KEY);
    
    if (savedUser && token && !this.isTokenExpired(token)) {
      const user = JSON.parse(savedUser);
      user.isAuthenticated = true;
      this.currentUserSubject.next(user);
    } else {
      // Clear invalid data
      this.clearAuthData();
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    const loginRequest: LoginRequest = { username, password };
    
    return this.http.post<LoginResponse[]>(`${environment.apiUrl}/auth/login`, loginRequest)
      .pipe(
        map(response => {
          // Backend returns array, get first element
          const loginResponse = Array.isArray(response) ? response[0] : response;
          
          if (loginResponse && loginResponse.accessToken) {
            this.setAuthData(loginResponse);
            return loginResponse;
          } else {
            throw new Error('Réponse invalide du serveur');
          }
        }),
        catchError(this.handleError)
      );
  }

  private setAuthData(loginResponse: LoginResponse): void {
    // Store tokens
    localStorage.setItem(this.TOKEN_KEY, loginResponse.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, loginResponse.refreshToken);
    
    // Prepare user data
    const user: User = {
      ...loginResponse.user,
      nomUtilisateur: loginResponse.user.nomUtilisateur,
      isAuthenticated: true
    };
    
    // Store user data
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  logout(): void {
    this.clearAuthData();
  }

  private clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const currentUser = this.currentUserSubject.value;
    return !!(token && !this.isTokenExpired(token) && currentUser?.isAuthenticated);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp < currentTime;
    } catch (error) {
      return true;
    }
  }

  refreshToken(): Observable<LoginResponse> {
    const refreshToken = this.getRefreshToken();
    
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('Aucun token de rafraîchissement disponible'));
    }

    return this.http.post<LoginResponse[]>(`${environment.apiUrl}/auth/refresh`, {}, {
      headers: {
        'Authorization': `Bearer ${refreshToken}`
      }
    }).pipe(
      map(response => {
        const refreshResponse = Array.isArray(response) ? response[0] : response;
        if (refreshResponse && refreshResponse.accessToken) {
          this.setAuthData(refreshResponse);
          return refreshResponse;
        } else {
          throw new Error('Échec du rafraîchissement du token');
        }
      }),
      catchError((error) => {
        this.logout();
        return this.handleError(error);
      })
    );
  }

  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'Une erreur est survenue lors de l\'authentification';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Server-side error
      switch (error.status) {
        case 401:
          errorMessage = 'Nom d\'utilisateur ou mot de passe incorrect';
          break;
        case 403:
          errorMessage = 'Accès refusé';
          break;
        case 404:
          errorMessage = 'Service non disponible';
          break;
        case 500:
          errorMessage = 'Erreur interne du serveur';
          break;
        case 0:
          errorMessage = 'Impossible de contacter le serveur';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.error?.message || error.message}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  };
}