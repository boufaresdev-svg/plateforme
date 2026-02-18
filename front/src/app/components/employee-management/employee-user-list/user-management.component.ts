import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserManagementService } from '../../../services/user-management.service';
import { SystemUser, UserStatus, Role, UserSearchQuery } from '../../../models/user-management.model';

@Component({
  selector: 'app-employee-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class EmployeeUserListComponent implements OnInit {
  users: SystemUser[] = [];
  roles: Role[] = [];
  filteredUsers: SystemUser[] = [];
  
  // Form states
  showAddForm = false;
  showEditForm = false;
  showRoleManagement = false;
  userForm: FormGroup;
  
  // Selected items
  selectedUser: SystemUser | null = null;
  selectedUserForRoles: SystemUser | null = null;
  selectedRoleIds: string[] = [];
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  
  // Search and filters
  searchQuery: UserSearchQuery = {
    page: 0,
    size: 10,
    sortBy: 'nomUtilisateur',
    sortDirection: 'ASC'
  };
  
  searchTerm = '';
  filterStatus: UserStatus | 'ALL' = 'ALL';
  
  // Available statuses
  userStatuses = Object.values(UserStatus);
  
  // Loading and error states
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  constructor(
    private userService: UserManagementService,
    private fb: FormBuilder
  ) {
    this.userForm = this.createUserForm();
  }

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
  }

  createUserForm(): FormGroup {
    return this.fb.group({
      id: [''],
      nomUtilisateur: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      motDePasse: ['', [Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      prenom: ['', [Validators.required, Validators.maxLength(50)]],
      nom: ['', [Validators.required, Validators.maxLength(50)]],
      numeroTelephone: ['', [Validators.maxLength(20)]],
      departement: ['', [Validators.maxLength(100)]],
      poste: ['', [Validators.maxLength(100)]],
      statut: [UserStatus.EN_ATTENTE, Validators.required]
    });
  }

  loadUsers(): void {
    this.loading = true;
    this.error = null;
    
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: (response) => {
        this.users = response.content || [];
        this.filteredUsers = this.users;
        this.totalPages = response.totalPages || 0;
        this.totalElements = response.totalElements || 0;
        this.currentPage = response.number || 0;
        this.loading = false;
        this.applyLocalFilters();
      },
      error: (error) => {
        this.error = 'Failed to load users. Please try again.';
        this.loading = false;
      }
    });
  }

  loadRoles(): void {
    this.userService.getAllRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
      },
      error: (error) => {
        // Error silently handled
      }
    });
  }

  applyLocalFilters(): void {
    let filtered = [...this.users];
    
    // Apply search term filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(user => 
        user.nomUtilisateur?.toLowerCase().includes(term) ||
        user.email?.toLowerCase().includes(term) ||
        user.prenom?.toLowerCase().includes(term) ||
        user.nom?.toLowerCase().includes(term) ||
        user.departement?.toLowerCase().includes(term)
      );
    }
    
    // Apply status filter
    if (this.filterStatus !== 'ALL') {
      filtered = filtered.filter(user => user.statut === this.filterStatus);
    }
    
    this.filteredUsers = filtered;
  }

  onSearchChange(): void {
    this.applyLocalFilters();
  }

  onStatusFilterChange(): void {
    this.applyLocalFilters();
  }

  // Pagination
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.searchQuery.page = this.currentPage + 1;
      this.loadUsers();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.searchQuery.page = this.currentPage - 1;
      this.loadUsers();
    }
  }

  goToPage(page: number): void {
    this.searchQuery.page = page;
    this.loadUsers();
  }

  // CRUD Operations
  openAddForm(): void {
    this.showAddForm = true;
    this.showEditForm = false;
    this.selectedRoleIds = [];
    this.userForm.reset({
      statut: UserStatus.EN_ATTENTE
    });
    this.userForm.get('motDePasse')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.userForm.get('motDePasse')?.updateValueAndValidity();
  }

  openEditForm(user: SystemUser): void {
    this.showEditForm = true;
    this.showAddForm = false;
    this.selectedUser = user;
    this.selectedRoleIds = user.roles?.map(role => role.id!).filter(id => id) || [];
    
    this.userForm.patchValue({
      id: user.id,
      nomUtilisateur: user.nomUtilisateur,
      email: user.email,
      prenom: user.prenom,
      nom: user.nom,
      numeroTelephone: user.numeroTelephone || '',
      departement: user.departement || '',
      poste: user.poste || '',
      statut: user.statut
    });
    
    // Password is optional for updates
    this.userForm.get('motDePasse')?.clearValidators();
    this.userForm.get('motDePasse')?.updateValueAndValidity();
  }

  closeForm(): void {
    this.showAddForm = false;
    this.showEditForm = false;
    this.selectedUser = null;
    this.selectedRoleIds = [];
    this.userForm.reset();
    this.error = null;
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.markFormGroupTouched(this.userForm);
      return;
    }

    const userData = this.userForm.value;
    
    // Add selected roles to userData
    userData.roleIds = this.selectedRoleIds;
    
    // Remove password if empty during update
    if (this.showEditForm && !userData.password) {
      delete userData.password;
    }

    this.loading = true;
    this.error = null;

    if (this.showAddForm) {
      this.userService.createUser(userData).subscribe({
        next: (response) => {
          this.successMessage = 'User created successfully with roles!';
          this.closeForm();
          this.loadUsers();
          this.clearSuccessMessage();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to create user. Please try again.';
          this.loading = false;
        }
      });
    } else if (this.showEditForm && userData.id) {
      this.userService.updateUser(userData.id, userData).subscribe({
        next: (response) => {
          this.successMessage = 'User and roles updated successfully!';
          this.closeForm();
          this.loadUsers();
          this.clearSuccessMessage();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to update user. Please try again.';
          this.loading = false;
        }
      });
    }
  }

  deleteUser(user: SystemUser): void {
    if (!user.id) return;
    
    if (confirm(`Are you sure you want to delete user "${user.nomUtilisateur}"?`)) {
      this.loading = true;
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.successMessage = 'User deleted successfully!';
          this.loadUsers();
          this.clearSuccessMessage();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to delete user. Please try again.';
          this.loading = false;
        }
      });
    }
  }

  // Role Management
  openRoleManagement(user: SystemUser): void {
    this.selectedUserForRoles = user;
    this.showRoleManagement = true;
  }

  closeRoleManagement(): void {
    this.showRoleManagement = false;
    this.selectedUserForRoles = null;
  }

  hasRole(user: SystemUser, roleId: string): boolean {
    return user.roles?.some(role => role.id === roleId) || false;
  }

  toggleRole(roleId: string): void {
    if (!this.selectedUserForRoles?.id || !roleId) return;

    const userId = this.selectedUserForRoles.id;
    const hasRole = this.hasRole(this.selectedUserForRoles, roleId);

    if (hasRole) {
      // Remove role
      this.userService.removeRoleFromUser(userId, roleId).subscribe({
        next: () => {
          this.successMessage = 'Role removed successfully!';
          this.loadUsers();
          this.clearSuccessMessage();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to remove role.';
        }
      });
    } else {
      // Assign role
      this.userService.assignRoleToUser(userId, roleId).subscribe({
        next: () => {
          this.successMessage = 'Role assigned successfully!';
          this.loadUsers();
          this.clearSuccessMessage();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to assign role.';
        }
      });
    }
  }

  // Toggle role selection in form
  toggleRoleSelection(roleId: string): void {
    const index = this.selectedRoleIds.indexOf(roleId);
    if (index > -1) {
      this.selectedRoleIds.splice(index, 1);
    } else {
      this.selectedRoleIds.push(roleId);
    }
  }

  isRoleSelected(roleId: string): boolean {
    return this.selectedRoleIds.includes(roleId);
  }

  // Utility methods
  getStatusClass(status: UserStatus): string {
    const classes: { [key in UserStatus]: string } = {
      [UserStatus.ACTIF]: 'status-active',
      [UserStatus.INACTIF]: 'status-inactive',
      [UserStatus.EN_ATTENTE]: 'status-pending',
      [UserStatus.SUSPENDU]: 'status-suspended'
    };
    return classes[status] || '';
  }

  getStatusIcon(status: UserStatus): string {
    const icons: { [key in UserStatus]: string } = {
      [UserStatus.ACTIF]: '✅',
      [UserStatus.INACTIF]: '⭕',
      [UserStatus.EN_ATTENTE]: '⏳',
      [UserStatus.SUSPENDU]: '🚫'
    };
    return icons[status] || '❓';
  }

  getRoleNames(user: SystemUser): string {
    if (!user.roles || user.roles.length === 0) {
      return 'No roles';
    }
    return user.roles.map((role: Role) => role.nom).join(', ');
  }

  formatDate(date: Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  private clearSuccessMessage(): void {
    setTimeout(() => {
      this.successMessage = null;
    }, 3000);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.userForm.get(fieldName);
    if (!field?.errors) return '';
    
    if (field.errors['required']) return `${fieldName} is required`;
    if (field.errors['email']) return 'Invalid email format';
    if (field.errors['minlength']) return `Minimum length is ${field.errors['minlength'].requiredLength}`;
    if (field.errors['maxlength']) return `Maximum length is ${field.errors['maxlength'].requiredLength}`;
    
    return 'Invalid field';
  }
}
