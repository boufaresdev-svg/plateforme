import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EmployeeService } from '../../services/employee_management/employee.service';
import { EmployeeStats } from '../../models/employee/employee.model';
import { EmployeeUserListComponent } from './employee-user-list/user-management.component';
import { RolePermissionManagementComponent } from './role-permission-management/role-permission-management.component';

@Component({
  selector: 'app-employee-management',
  standalone: true,
  imports: [CommonModule, EmployeeUserListComponent, RolePermissionManagementComponent],
  templateUrl: './employee-management.component.html',
  styleUrls: ['./employee-management.component.css']
})
export class EmployeeManagementComponent implements OnInit {
  stats: EmployeeStats = {
    totalEmployees: 0,
    activeEmployees: 0,
    inactiveEmployees: 0,
    suspendedEmployees: 0,
    pendingEmployees: 0,
    totalRoles: 0,
    totalPermissions: 0
  };

  activeView: 'users' | 'roles' = 'users';

  constructor(
    private employeeService: EmployeeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.employeeService.getEmployeeStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        // Error silently handled
      }
    });
  }

  switchView(view: 'users' | 'roles'): void {
    this.activeView = view;
  }

  goBack(): void {
    this.router.navigate(['/menu']);
  }
}
