import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environment/environement';
import { 
  Employee, 
  Role, 
  Permission, 
  EmployeeStats,
  PageResponse,
  PaginationParams
} from '../../models/employee/employee.model';

// Response types matching backend CQRS
interface CommandResponse {
  id: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = `${environment.apiUrl}/users`;
  private roleApiUrl = `${environment.apiUrl}/roles`;
  private permissionApiUrl = `${environment.apiUrl}/permissions`;

  constructor(private http: HttpClient) {}

  // Employee CRUD operations
  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl);
  }

  getEmployeesPaginated(params: PaginationParams): Observable<PageResponse<Employee>> {
    // Use POST /api/users/search for paginated results with filters
    const searchRequest = {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'createdAt',
      sortDirection: params.sortDirection || 'DESC',
      search: params.search || '',
      status: params.status
    };

    return this.http.post<PageResponse<Employee>>(`${this.apiUrl}/search`, searchRequest);
  }

  getEmployeeById(id: string): Observable<Employee> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => {
        // Handle array response [[{...}]] and extract first element
        let employee = Array.isArray(response[0]) ? response[0][0] : (Array.isArray(response) ? response[0] : response);
        // Ensure roles and permissions arrays are not null
        return {
          ...employee,
          roles: (employee.roles || []).map((role: any) => ({
            ...role,
            permissions: role.permissions || []
          }))
        };
      })
    );
  }

  createEmployee(employee: Employee): Observable<CommandResponse> {
    const command = {
      nomUtilisateur: employee.nomUtilisateur,
      email: employee.email,
      motDePasse: employee.motDePasse,
      prenom: employee.prenom,
      nom: employee.nom,
      numeroTelephone: employee.numeroTelephone,
      departement: employee.departement,
      poste: employee.poste,
      statut: employee.statut,
      roleIds: employee.roles?.map(r => r.id).filter(id => id) || []
    };
    return this.http.post<CommandResponse>(this.apiUrl, command);
  }

  updateEmployee(id: string, employee: Employee): Observable<CommandResponse> {
    const command = {
      id: id,
      nomUtilisateur: employee.nomUtilisateur,
      email: employee.email,
      prenom: employee.prenom,
      nom: employee.nom,
      numeroTelephone: employee.numeroTelephone,
      departement: employee.departement,
      poste: employee.poste,
      statut: employee.statut,
      roleIds: employee.roles?.map(r => r.id).filter(id => id) || []
    };
    return this.http.put<CommandResponse>(`${this.apiUrl}/${id}`, command);
  }

  deleteEmployee(id: string): Observable<CommandResponse> {
    return this.http.delete<CommandResponse>(`${this.apiUrl}/${id}`);
  }

  getEmployeeStats(): Observable<EmployeeStats> {
    return this.http.get<EmployeeStats>(`${this.apiUrl}/stats`);
  }

  // Role CRUD operations
  getAllRoles(): Observable<Role[]> {
    return this.http.get<any>(this.roleApiUrl).pipe(
      map(response => {
        console.log('🔍 [ROLES DEBUG] Raw response:', response);
        console.log('🔍 [ROLES DEBUG] Response type - isArray:', Array.isArray(response), 'has [0]:', response?.[0]);
        
        // Handle different response formats based on what we observed in the API test:
        // The API returns: [[{...}]] - double array wrap
        let roles: any[];
        
        if (Array.isArray(response) && Array.isArray(response[0])) {
          // Format: [[{...}]] - double nested array (this is what our API returns)
          roles = response[0];
          console.log('📦 [ROLES DEBUG] Extracted from double array:', roles);
        } else if (!Array.isArray(response) && response && typeof response === 'object' && '0' in response) {
          // Format: {"0": [...]} - plain object with numeric key
          roles = response['0'];
          console.log('📦 [ROLES DEBUG] Extracted from object wrapper:', roles);
        } else if (Array.isArray(response)) {
          // Format: [{...}] - normal array
          roles = response;
          console.log('📦 [ROLES DEBUG] Already array format:', roles);
        } else {
          console.error('❌ [ROLES DEBUG] Unknown response format:', response);
          roles = [];
        }
        
        // Ensure permissions is an array, not null
        const processedRoles = roles.map((role: any) => ({
          ...role,
          permissions: role.permissions || []
        }));
        
        console.log('✅ [ROLES DEBUG] Final processed roles:', processedRoles);
        console.log('✅ [ROLES DEBUG] Number of roles:', processedRoles.length);
        return processedRoles;
      })
    );
  }

  getRoleById(id: string): Observable<Role> {
    return this.http.get<any>(`${this.roleApiUrl}/${id}`).pipe(
      map(response => {
        // Handle array response [[{...}]] and extract first element
        let role = Array.isArray(response[0]) ? response[0][0] : (Array.isArray(response) ? response[0] : response);
        // Ensure permissions is an array, not null
        return {
          ...role,
          permissions: role.permissions || []
        };
      })
    );
  }

  createRole(role: Role): Observable<CommandResponse> {
    const command = {
      nom: role.nom,
      description: role.description,
      permissionIds: role.permissions?.map(p => p.id).filter(id => id) || []
    };
    return this.http.post<CommandResponse>(this.roleApiUrl, command);
  }

  updateRole(id: string, role: Role): Observable<CommandResponse> {
    const command = {
      id: id,
      nom: role.nom,
      description: role.description,
      permissionIds: role.permissions?.map(p => p.id).filter(id => id) || []
    };
    return this.http.put<CommandResponse>(`${this.roleApiUrl}/${id}`, command);
  }

  deleteRole(id: string): Observable<CommandResponse> {
    return this.http.delete<CommandResponse>(`${this.roleApiUrl}/${id}`);
  }

  assignRoleToEmployee(employeeId: string, roleId: string): Observable<CommandResponse> {
    return this.http.post<CommandResponse>(`${this.apiUrl}/${employeeId}/roles/${roleId}`, {});
  }

  removeRoleFromEmployee(employeeId: string, roleId: string): Observable<CommandResponse> {
    return this.http.delete<CommandResponse>(`${this.apiUrl}/${employeeId}/roles/${roleId}`);
  }

  // Permission CRUD operations
  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<any>(this.permissionApiUrl).pipe(
      map(response => {
        console.log('🔍 Raw permissions response:', response);
        console.log('🔍 Permissions type check - isArray:', Array.isArray(response), 'has [0]:', response?.[0]);
        
        // Handle different response formats:
        // 1. [[{...}]] - double array wrap (check this FIRST before object check)
        // 2. {"0": [{...}]} - plain object (not array) with "0" key
        // 3. [{...}] - normal array
        let permissions: any[];
        
        if (Array.isArray(response) && Array.isArray(response[0])) {
          // Format: [[{...}]] - double nested array
          permissions = response[0];
          console.log('📦 Extracted permissions from double array:', permissions);
        } else if (!Array.isArray(response) && response && typeof response === 'object' && '0' in response) {
          // Format: {"0": [...]} - plain object with numeric key
          permissions = response['0'];
          console.log('📦 Extracted permissions from object wrapper:', permissions);
        } else if (Array.isArray(response)) {
          // Format: [{...}] - normal array
          permissions = response;
          console.log('📦 Permissions already array format:', permissions);
        } else {
          console.error('❌ Unknown permissions response format:', response);
          permissions = [];
        }
        
        console.log('✅ Final processed permissions:', permissions);
        return permissions;
      })
    );
  }

  getPermissionById(id: string): Observable<Permission> {
    return this.http.get<any>(`${this.permissionApiUrl}/${id}`).pipe(
      map(response => {
        // Handle array response [[{...}]] and extract first element
        return Array.isArray(response[0]) ? response[0][0] : (Array.isArray(response) ? response[0] : response);
      })
    );
  }

  // Permission CRUD operations commented out - Permissions are auto-generated
  /*
  createPermission(permission: Permission): Observable<CommandResponse> {
    const command = {
      ressource: permission.ressource,
      action: permission.action,
      description: permission.description,
      nomAffichage: permission.nomAffichage
    };
    return this.http.post<CommandResponse>(this.permissionApiUrl, command);
  }

  updatePermission(id: string, permission: Permission): Observable<CommandResponse> {
    const command = {
      id: id,
      ressource: permission.ressource,
      action: permission.action,
      description: permission.description,
      nomAffichage: permission.nomAffichage
    };
    return this.http.put<CommandResponse>(`${this.permissionApiUrl}/${id}`, command);
  }

  deletePermission(id: string): Observable<CommandResponse> {
    return this.http.delete<CommandResponse>(`${this.permissionApiUrl}/${id}`);
  }
  */

  assignPermissionToRole(roleId: string, permissionId: string): Observable<CommandResponse> {
    return this.http.post<CommandResponse>(`${this.roleApiUrl}/${roleId}/permissions/${permissionId}`, {});
  }

  removePermissionFromRole(roleId: string, permissionId: string): Observable<CommandResponse> {
    return this.http.delete<CommandResponse>(`${this.roleApiUrl}/${roleId}/permissions/${permissionId}`);
  }
}
