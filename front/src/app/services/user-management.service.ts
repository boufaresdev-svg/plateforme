import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SystemUser, UserSearchQuery, UserStats, Role, Permission, ModuleInfo, PermissionsByModule } from '../models/user-management.model';
import { environment } from '../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private apiUrl = `${environment.apiUrl}/users`;
  private rolesApiUrl = `${environment.apiUrl}/roles`;
  private permissionsApiUrl = `${environment.apiUrl}/permissions`;

  constructor(private http: HttpClient) {}

  // Get user statistics
  getUserStats(): Observable<UserStats> {
    return this.http.get<any[]>(`${this.apiUrl}/stats`).pipe(
      map(response => response[0] as UserStats)
    );
  }

  // Get user by ID
  getUserById(id: string): Observable<SystemUser> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}`).pipe(
      map(response => response[0] as SystemUser)
    );
  }

  // Search and filter users with pagination
  searchUsers(query: UserSearchQuery = {}): Observable<any> {
    return this.http.post<any[]>(`${this.apiUrl}/search`, query).pipe(
      map(response => response[0])
    );
  }

  // Create new user
  createUser(user: SystemUser): Observable<any> {
    return this.http.post<any[]>(this.apiUrl, user).pipe(
      map(response => response[0])
    );
  }

  // Update existing user
  updateUser(id: string, user: SystemUser): Observable<any> {
    return this.http.put<any[]>(`${this.apiUrl}/${id}`, user).pipe(
      map(response => response[0])
    );
  }

  // Delete user
  deleteUser(id: string): Observable<any> {
    return this.http.delete<any[]>(`${this.apiUrl}/${id}`).pipe(
      map(response => response[0])
    );
  }

  // Assign role to user
  assignRoleToUser(userId: string, roleId: string): Observable<any> {
    return this.http.post<any[]>(`${this.apiUrl}/${userId}/roles/${roleId}`, {}).pipe(
      map(response => response[0])
    );
  }

  // Remove role from user
  removeRoleFromUser(userId: string, roleId: string): Observable<any> {
    return this.http.delete<any[]>(`${this.apiUrl}/${userId}/roles/${roleId}`).pipe(
      map(response => response[0])
    );
  }

  // Get all roles (for dropdown/assignment)
  getAllRoles(): Observable<Role[]> {
    return this.http.get<any[]>(this.rolesApiUrl).pipe(
      map(response => response[0] as Role[])
    );
  }

  // Get role by ID
  getRoleById(id: string): Observable<Role> {
    return this.http.get<any[]>(`${this.rolesApiUrl}/${id}`).pipe(
      map(response => response[0] as Role)
    );
  }

  // Get all modules with permission counts
  getAllModules(): Observable<ModuleInfo[]> {
    return this.http.get<any>(`${this.permissionsApiUrl}/modules`).pipe(
      map(response => {
        console.log('🌐 Raw modules response:', response);
        console.log('🌐 Modules response is array?', Array.isArray(response));
        
        // Handle double-wrapped array if needed
        if (Array.isArray(response) && response.length > 0) {
          if (Array.isArray(response[0])) {
            console.log('🌐 Unwrapping double modules array...');
            return response[0] as ModuleInfo[];
          }
        }
        
        return response as ModuleInfo[];
      })
    );
  }

  // Get all permissions
  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<any>(this.permissionsApiUrl).pipe(
      map(response => {
        console.log('🌐 Raw permissions response:', response);
        console.log('🌐 Response is array?', Array.isArray(response));
        console.log('🌐 Response[0] is array?', Array.isArray(response[0]));
        
        // Handle double-wrapped array response from backend
        if (Array.isArray(response) && response.length > 0) {
          if (Array.isArray(response[0])) {
            console.log('🌐 Unwrapping double array...');
            return response[0] as Permission[];
          }
        }
        
        return response as Permission[];
      })
    );
  }

  // Get permissions grouped by module
  getPermissionsByModule(): Observable<PermissionsByModule> {
    return this.getAllPermissions().pipe(
      map(permissions => {
        const grouped: PermissionsByModule = {};
        permissions.forEach(permission => {
          const moduleKey = permission.module || 'SYSTEME';
          if (!grouped[moduleKey]) {
            grouped[moduleKey] = [];
          }
          grouped[moduleKey].push(permission);
        });
        return grouped;
      })
    );
  }

  // Get permissions for a specific module
  getPermissionsByModuleCode(moduleCode: string): Observable<Permission[]> {
    return this.http.get<Permission[]>(`${this.permissionsApiUrl}/modules/${moduleCode}`);
  }
}
