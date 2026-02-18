import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnDestroy {
  credentials = {
    username: '',
    password: ''
  };
  
  errorMessage = '';
  successMessage = '';
  isLoading = false;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/menu']);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onLogin(): void {
    // Clear previous messages
    this.errorMessage = '';
    this.successMessage = '';

    // Validate inputs
    if (!this.credentials.username?.trim() || !this.credentials.password?.trim()) {
      this.errorMessage = 'Veuillez saisir votre nom d\'utilisateur et mot de passe';
      this.shakeForm();
      return;
    }
    if (this.credentials.username.trim().length < 3) {
      this.errorMessage = 'Le nom d\'utilisateur doit contenir au moins 3 caractères';
      this.shakeForm();
      return;
    }
    if (this.credentials.password.length < 4) {
      this.errorMessage = 'Le mot de passe doit contenir au moins 4 caractères';
      this.shakeForm();
      return;
    }

    this.isLoading = true;

    this.authService.login(this.credentials.username.trim(), this.credentials.password)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = 'Connexion réussie ! Redirection en cours...';
          
          this.credentials.password = '';
          
          setTimeout(() => {
            this.router.navigate(['/menu']);
          }, 1200);
        },
        error: (error) => {
          this.isLoading = false;
          
          if (error.status === 401) {
            this.errorMessage = 'Identifiants incorrects. Veuillez réessayer.';
          } else if (error.status === 403) {
            this.errorMessage = 'Accès refusé. Contactez l\'administrateur.';
          } else if (error.status === 0) {
            this.errorMessage = 'Impossible de se connecter au serveur. Vérifiez votre connexion.';
          } else if (error.status >= 500) {
            this.errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          } else {
            this.errorMessage = error.message || 'Erreur de connexion. Veuillez réessayer.';
          }
          
          this.shakeForm();
          console.error('Login error:', error);
          
          this.credentials.password = '';
        }
      });
  }


  togglePasswordVisibility(input: HTMLInputElement): void {
    if (this.isLoading) return;
    
    input.type = input.type === 'password' ? 'text' : 'password';
    
    // Update icon (if you want to change the eye icon)
    const button = input.nextElementSibling as HTMLElement;
    if (button) {
      button.setAttribute('aria-label', 
        input.type === 'password' ? 'Afficher le mot de passe' : 'Masquer le mot de passe'
      );
    }
  }

  onForgotPassword(event: Event): void {
    event.preventDefault();
    
    if (this.isLoading) return;

    this.errorMessage = '';
    this.successMessage = 'Veuillez contacter l\'administrateur pour réinitialiser votre mot de passe.';

  }

  /**
   * Shake form animation for errors
   */
  private shakeForm(): void {

  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !this.isLoading) {
      this.onLogin();
    }
  }


  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }


  sanitizeUsername(): void {
    this.credentials.username = this.credentials.username
      .replace(/[<>]/g, '') 
      .trim();
  }
}