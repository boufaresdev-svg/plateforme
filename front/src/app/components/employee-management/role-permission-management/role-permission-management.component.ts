        import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmployeeService } from '../../../services/employee_management/employee.service';
import { UserManagementService } from '../../../services/user-management.service';
import { Role, Permission, PermissionAction } from '../../../models/employee/employee.model';
import { ModuleInfo, PermissionsByModule } from '../../../models/user-management.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-role-permission-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './role-permission-management.component.html',
  styleUrls: ['./role-permission-management.component.css']
})
export class RolePermissionManagementComponent implements OnInit {
  @Output() statsUpdated = new EventEmitter<void>();

  roles: Role[] = [];
  permissions: Permission[] = [];
  filteredRoles: Role[] = [];
  filteredPermissions: Permission[] = [];
  
  // Module grouping
  modules: ModuleInfo[] = [];
  permissionsByModule: { [moduleCode: string]: Permission[] } = {};
  expandedModules: Set<string> = new Set();
  expandedModulesInModal: Set<string> = new Set();
  
  roleForm!: FormGroup;
  permissionForm!: FormGroup;
  
  searchTermRole: string = '';
  searchTermPermission: string = '';
  modalSearchTerm: string = '';
  
  showRoleModal: boolean = false;
  showPermissionModal: boolean = false;
  isEditMode: boolean = false;
  selectedRole: Role | null = null;
  selectedPermission: Permission | null = null;
  
  activeTab: 'roles' | 'permissions' = 'roles';
  
  PermissionAction = PermissionAction;
  actionOptions = Object.values(PermissionAction);
  
  // Module display names
  moduleDisplayNames: { [key: string]: string } = {
    'GESTION_UTILISATEURS': 'Gestion des Utilisateurs',
    'GESTION_CLIENTS': 'Gestion des Clients',
    'GESTION_FOURNISSEURS': 'Gestion des Fournisseurs',
    'GESTION_PROJETS': 'Gestion des Projets',
    'GESTION_RH': 'Gestion des Ressources Humaines',
    'GESTION_FORMATIONS': 'Gestion des Formations',
    'GESTION_VEHICULES': 'Gestion des Véhicules',
    'GESTION_STOCK': 'Gestion du Stock',
    'SYSTEME': 'Système et Configuration'
  };

  constructor(
    private employeeService: EmployeeService,
    private userManagementService: UserManagementService,
    private fb: FormBuilder
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadRoles();
    this.loadPermissions();
    this.loadModules();
  }

  initForms(): void {
    this.roleForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      permissions: [[]]
    });

    this.permissionForm = this.fb.group({
      ressource: ['', Validators.required],
      action: [PermissionAction.LIRE, Validators.required],
      description: [''],
      nomAffichage: [''],
      module: ['', Validators.required]
    });
  }

  loadRoles(): void {
    this.employeeService.getAllRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.filteredRoles = roles;
        this.statsUpdated.emit();
      },
      error: (error) => {
        Swal.fire('Erreur', 'Erreur lors du chargement des rôles', 'error');
      }
    });
  }

  loadPermissions(): void {
    // Use userManagementService to get permissions with module field
    this.userManagementService.getAllPermissions().subscribe({
      next: (permissions) => {
        this.permissions = permissions;
        this.filteredPermissions = permissions;
        this.groupPermissionsByModule();
        this.statsUpdated.emit();
      },
      error: (error) => {
        Swal.fire('Erreur', 'Erreur lors du chargement des permissions', 'error');
      }
    });
  }

  loadModules(): void {
    this.userManagementService.getAllModules().subscribe({
      next: (modules) => {
        this.modules = modules;
        // Expand first module by default
        if (modules.length > 0) {
          this.expandedModules.add(modules[0].code);
        }
      },
      error: (error) => {
        Swal.fire('Erreur', 'Erreur lors du chargement des modules', 'error');
      }
    });
  }

  groupPermissionsByModule(): void {
    // Group permissions by module
    this.permissionsByModule = {};
    
    this.permissions.forEach(permission => {
      const moduleCode = permission.module || 'SYSTEME'; // Default to SYSTEME if no module
      if (!this.permissionsByModule[moduleCode]) {
        this.permissionsByModule[moduleCode] = [];
      }
      this.permissionsByModule[moduleCode].push(permission);
    });
  }

  toggleModule(moduleCode: string): void {
    if (this.expandedModules.has(moduleCode)) {
      this.expandedModules.delete(moduleCode);
    } else {
      this.expandedModules.add(moduleCode);
    }
  }

  isModuleExpanded(moduleCode: string): boolean {
    return this.expandedModules.has(moduleCode);
  }

  getModuleDisplayName(moduleCode: string): string {
    return this.moduleDisplayNames[moduleCode] || moduleCode;
  }

  getModuleIcon(moduleCode: string): string {
    const icons: { [key: string]: string } = {
      'GESTION_UTILISATEURS': 'fa-users-cog',
      'GESTION_CLIENTS': 'fa-user-tie',
      'GESTION_FOURNISSEURS': 'fa-truck',
      'GESTION_PROJETS': 'fa-project-diagram',
      'GESTION_RH': 'fa-user-friends',
      'GESTION_FORMATIONS': 'fa-graduation-cap',
      'GESTION_VEHICULES': 'fa-car',
      'SYSTEME': 'fa-cogs'
    };
    return icons[moduleCode] || 'fa-folder';
  }

  getPermissionsForModule(moduleCode: string): Permission[] {
    return this.permissionsByModule[moduleCode] || [];
  }

  getFilteredPermissionsForModule(moduleCode: string): Permission[] {
    const modulePermissions = this.getPermissionsForModule(moduleCode);
    
    if (!this.searchTermPermission) {
      return modulePermissions;
    }

    const term = this.searchTermPermission.toLowerCase();
    return modulePermissions.filter(permission => 
      (permission.nomAffichage && permission.nomAffichage.toLowerCase().includes(term)) ||
      permission.ressource.toLowerCase().includes(term) ||
      permission.action.toLowerCase().includes(term) ||
      (permission.description && permission.description.toLowerCase().includes(term))
    );
  }

  onSearchRole(): void {
    const term = this.searchTermRole.toLowerCase();
    this.filteredRoles = this.roles.filter(role => {
      const roleName = this.getRoleName(role).toLowerCase();
      const roleDesc = this.getRoleDescription(role).toLowerCase();
      return roleName.includes(term) || roleDesc.includes(term);
    });
  }

  onSearchPermission(): void {
    const term = this.searchTermPermission.toLowerCase();
    this.filteredPermissions = this.permissions.filter(permission => 
      (permission.nomAffichage && permission.nomAffichage.toLowerCase().includes(term)) ||
      permission.ressource.toLowerCase().includes(term) ||
      permission.action.toLowerCase().includes(term) ||
      (permission.description && permission.description.toLowerCase().includes(term))
    );
  }

  // Role Management
  openAddRoleModal(): void {
    this.isEditMode = false;
    this.selectedRole = null;
    this.roleForm.reset({
      permissions: []
    });
    this.modalSearchTerm = '';
    this.expandedModulesInModal.clear();
    // Expand first module by default
    if (this.modules.length > 0) {
      this.expandedModulesInModal.add(this.modules[0].code);
    }
    this.showRoleModal = true;
  }

  openEditRoleModal(role: Role): void {
    this.isEditMode = true;
    this.selectedRole = role;
    this.roleForm.patchValue({
      nom: this.getRoleName(role),
      description: this.getRoleDescription(role),
      permissions: this.getRolePermissions(role).map(p => p.id)
    });
    this.modalSearchTerm = '';
    this.expandedModulesInModal.clear();
    // Expand modules that have selected permissions
    this.modules.forEach(module => {
      if (this.getSelectedPermissionsCountForModule(module.code) > 0) {
        this.expandedModulesInModal.add(module.code);
      }
    });
    this.showRoleModal = true;
  }

  closeRoleModal(): void {
    this.showRoleModal = false;
    this.selectedRole = null;
    this.roleForm.reset();
  }

  saveRole(): void {
    if (this.roleForm.invalid) {
      Swal.fire('Erreur', 'Veuillez remplir tous les champs requis', 'error');
      return;
    }

    const formValue = this.roleForm.value;
    const selectedPermissions = this.permissions.filter(p => formValue.permissions.includes(p.id));
    
    const roleData: Role = {
      nom: formValue.nom,
      description: formValue.description,
      permissions: selectedPermissions
    };

    if (this.isEditMode && this.selectedRole?.id) {
      this.employeeService.updateRole(this.selectedRole.id, roleData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Rôle modifié avec succès', 'success');
          this.loadRoles();
          this.closeRoleModal();
        },
        error: (error) => {
          Swal.fire('Erreur', 'Erreur lors de la modification du rôle', 'error');
        }
      });
    } else {
      this.employeeService.createRole(roleData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Rôle ajouté avec succès', 'success');
          this.loadRoles();
          this.closeRoleModal();
        },
        error: (error) => {
          Swal.fire('Erreur', 'Erreur lors de l\'ajout du rôle', 'error');
        }
      });
    }
  }

  deleteRole(role: Role): void {
    if (!role.id) return;

    Swal.fire({
      title: 'Êtes-vous sûr?',
      text: `Voulez-vous supprimer le rôle "${this.getRoleName(role)}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed && role.id) {
        this.employeeService.deleteRole(role.id).subscribe({
          next: () => {
            Swal.fire('Supprimé!', 'Le rôle a été supprimé.', 'success');
            this.loadRoles();
          },
          error: (error) => {
            Swal.fire('Erreur', 'Erreur lors de la suppression du rôle', 'error');
          }
        });
      }
    });
  }

  // Permission Management - CRUD operations removed (permissions are auto-generated)
  /*
  openAddPermissionModal(): void {
    this.isEditMode = false;
    this.selectedPermission = null;
    this.permissionForm.reset({
      action: PermissionAction.LIRE
    });
    this.showPermissionModal = true;
  }

  openEditPermissionModal(permission: Permission): void {
    this.isEditMode = true;
    this.selectedPermission = permission;
    this.permissionForm.patchValue({
      ressource: permission.ressource,
      action: permission.action,
      description: permission.description,
      nomAffichage: permission.nomAffichage,
      module: permission.module || ''
    });
    this.showPermissionModal = true;
  }

  closePermissionModal(): void {
    this.showPermissionModal = false;
    this.selectedPermission = null;
    this.permissionForm.reset();
  }

  savePermission(): void {
    if (this.permissionForm.invalid) {
      Swal.fire('Erreur', 'Veuillez remplir tous les champs requis', 'error');
      return;
    }

    const permissionData: Permission = this.permissionForm.value;

    if (this.isEditMode && this.selectedPermission?.id) {
      this.employeeService.updatePermission(this.selectedPermission.id, permissionData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Permission modifiée avec succès', 'success');
          this.loadPermissions();
          this.closePermissionModal();
        },
        error: (error) => {
          Swal.fire('Erreur', 'Erreur lors de la modification de la permission', 'error');
        }
      });
    } else {
      this.employeeService.createPermission(permissionData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Permission ajoutée avec succès', 'success');
          this.loadPermissions();
          this.closePermissionModal();
        },
        error: (error) => {
          Swal.fire('Erreur', 'Erreur lors de l\'ajout de la permission', 'error');
        }
      });
    }
  }

  deletePermission(permission: Permission): void {
    if (!permission.id) return;

    Swal.fire({
      title: 'Êtes-vous sûr?',
      text: `Voulez-vous supprimer la permission "${permission.nomAffichage || permission.ressource + '_' + permission.action}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed && permission.id) {
        this.employeeService.deletePermission(permission.id).subscribe({
          next: () => {
            Swal.fire('Supprimé!', 'La permission a été supprimée.', 'success');
            this.loadPermissions();
          },
          error: (error) => {
            Swal.fire('Erreur', 'Erreur lors de la suppression de la permission', 'error');
          }
        });
      }
    });
  }
  */

  onPermissionCheckboxChange(event: any, permissionId: string | undefined): void {
    if (!permissionId) return;
    
    const permissionsArray = this.roleForm.get('permissions')?.value || [];
    
    if (event.target.checked) {
      if (!permissionsArray.includes(permissionId)) {
        this.roleForm.patchValue({
          permissions: [...permissionsArray, permissionId]
        });
      }
    } else {
      this.roleForm.patchValue({
        permissions: permissionsArray.filter((id: string) => id !== permissionId)
      });
    }
  }

  // Modal-specific module methods
  toggleModuleInModal(moduleCode: string): void {
    if (this.expandedModulesInModal.has(moduleCode)) {
      this.expandedModulesInModal.delete(moduleCode);
    } else {
      this.expandedModulesInModal.add(moduleCode);
    }
  }

  isModuleExpandedInModal(moduleCode: string): boolean {
    return this.expandedModulesInModal.has(moduleCode);
  }

  /**
   * Click handler for the module header inside the modal.
   * We ignore clicks on the checkbox, its label or the toggle button so
   * those elements keep their native behavior. Clicking elsewhere on the
   * header toggles the module expand/collapse state.
   */
  onModuleHeaderClick(event: Event, moduleCode: string): void {
    const target = event.target as HTMLElement | null;
    if (!target) return;

    // If the click came from the checkbox, its label, or a button, do nothing
    if (target.closest('input[type="checkbox"]') || target.closest('label') || target.closest('button')) {
      return;
    }

    this.toggleModuleInModal(moduleCode);
  }

  onModuleCheckboxChange(event: any, moduleCode: string): void {
    const modulePermissions = this.getPermissionsForModule(moduleCode);
    const permissionsArray = this.roleForm.get('permissions')?.value || [];
    
    if (event.target.checked) {
      // Add all permissions from this module
      const allModulePermissionIds = modulePermissions.map(p => p.id).filter(id => id !== undefined) as string[];
      const newPermissions = [...new Set([...permissionsArray, ...allModulePermissionIds])];
      this.roleForm.patchValue({
        permissions: newPermissions
      });
    } else {
      // Remove all permissions from this module
      const modulePermissionIds = modulePermissions.map(p => p.id);
      const newPermissions = permissionsArray.filter((id: string) => !modulePermissionIds.includes(id));
      this.roleForm.patchValue({
        permissions: newPermissions
      });
    }
  }

  isModuleFullySelected(moduleCode: string): boolean {
    const modulePermissions = this.getPermissionsForModule(moduleCode);
    const permissionsArray = this.roleForm.get('permissions')?.value || [];
    
    if (modulePermissions.length === 0) return false;
    
    return modulePermissions.every(p => p.id && permissionsArray.includes(p.id));
  }

  isModulePartiallySelected(moduleCode: string): boolean {
    const modulePermissions = this.getPermissionsForModule(moduleCode);
    const permissionsArray = this.roleForm.get('permissions')?.value || [];
    
    if (modulePermissions.length === 0) return false;
    
    const selectedCount = modulePermissions.filter(p => p.id && permissionsArray.includes(p.id)).length;
    return selectedCount > 0 && selectedCount < modulePermissions.length;
  }

  getSelectedPermissionsCountForModule(moduleCode: string): number {
    const modulePermissions = this.getPermissionsForModule(moduleCode);
    const permissionsArray = this.roleForm.get('permissions')?.value || [];
    
    return modulePermissions.filter(p => p.id && permissionsArray.includes(p.id)).length;
  }

  getFilteredPermissionsForModuleInModal(moduleCode: string): Permission[] {
    const modulePermissions = this.getPermissionsForModule(moduleCode);
    
    if (!this.modalSearchTerm) {
      return modulePermissions;
    }
    
    const term = this.modalSearchTerm.toLowerCase();
    return modulePermissions.filter(permission => {
      const displayName = this.getPermissionDisplayName(permission).toLowerCase();
      const resource = this.getPermissionResource(permission).toLowerCase();
      const action = permission.action?.toLowerCase() || '';
      
      return displayName.includes(term) || resource.includes(term) || action.includes(term);
    });
  }

  getActionBadgeClass(action: PermissionAction): string {
    switch (action) {
      case PermissionAction.CREER:
        return 'badge-success';
      case PermissionAction.LIRE:
        return 'badge-info';
      case PermissionAction.MODIFIER:
        return 'badge-warning';
      case PermissionAction.SUPPRIMER:
        return 'badge-danger';
      case PermissionAction.EXPORTER:
        return 'badge-primary';
      case PermissionAction.IMPORTER:
        return 'badge-secondary';
      case PermissionAction.LISTER_SENSIBLE:
        return 'badge-dark';
      default:
        return 'badge-secondary';
    }
  }

  getActionLabel(action: PermissionAction): string {
    const labels: Record<PermissionAction, string> = {
      [PermissionAction.CREER]: 'Créer',
      [PermissionAction.LIRE]: 'Consulter',
      [PermissionAction.MODIFIER]: 'Modifier',
      [PermissionAction.SUPPRIMER]: 'Supprimer',
      [PermissionAction.EXPORTER]: 'Exporter',
      [PermissionAction.IMPORTER]: 'Importer',
      [PermissionAction.LISTER_SENSIBLE]: 'Données Sensibles'
    };
    return labels[action] || action;
  }

  // Helper methods to handle field name variations (backend returns camelCase)
  getPermissionResource(permission: Permission): string {
    return (permission as any).resource || (permission as any).ressource || '';
  }

  getPermissionDisplayName(permission: Permission): string {
    const resource = this.getPermissionResource(permission);
    return (permission as any).displayName || (permission as any).nomAffichage || 
           `${resource}_${permission.action}`;
  }

  // Helper methods for role field name variations
  getRoleName(role: Role): string {
    return (role as any).name || (role as any).nom || '';
  }

  getRoleDescription(role: Role): string {
    return role.description || 'Aucune description';
  }

  getRolePermissionsCount(role: Role): number {
    return role.permissions?.length || 0;
  }

  getRolePermissions(role: Role): Permission[] {
    return role.permissions || [];
  }
}
