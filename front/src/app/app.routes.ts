import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { MenuComponent } from './components/menu/menu.component';
import { VehicleManagementComponent } from './components/vehicle-management/vehicle-management.component';
import { HRManagementComponent } from './components/hr-management/hr-management.component';
import { StockManagementComponent } from './components/stock-management/stock-management.component';
import { PretManagementComponent } from './components/pret-management/pret-management.component';
import { SousTraitanceManagementComponent } from './components/sous-traitance-management/sous-traitance-management.component';
import { DossierManagementComponent } from './components/dossier-management/dossier-management.component';
import { FormationManagementComponent } from './components/formation-management/formation-management.component';
import { SOPManagementComponent } from './components/sop-management/sop-management.component';
import { ClientManagementComponent } from './components/client-management/client-management.component';
import { FournisseurManagementComponent } from './components/fournisseur-management/fournisseur-management.component';
import { ProjetManagementNewComponent } from './components/projet-management-new/projet-management-new.component';
import { EmployeeManagementComponent } from './components/employee-management/employee-management.component';
import { AuthGuard } from './guards/auth.guard';
import { ManageFormationComponent } from './components/formation-management/formations/manage-formation/manage-formation.component';
import { ContenuDetailleComponent } from './components/formation-management/contenu-detaille/contenu-detaille.component';
import { ApprenantManagementComponent } from './components/formation-management/apprenants/apprenant-management.component';

export const routes: Routes = [
  { 
    path: '', 
    redirectTo: '/login', 
    pathMatch: 'full' 
  },
  { 
    path: 'login', 
    component: LoginComponent 
  },
  
  { 
    path: 'menu', 
    component: MenuComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'vehicules',
    component: VehicleManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'rh',
    component: HRManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'stock',
    component: StockManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'prets',
    component: PretManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'sous-traitance',
    component: SousTraitanceManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dossiers',
    component: DossierManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'formation',
    component: FormationManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'formation/manage',
    component: ManageFormationComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'formation/contenu',
    component: ContenuDetailleComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'formation/contenu/:id',
    component: ContenuDetailleComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'formation/apprenants',
    component: ApprenantManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'formation/:formationId/apprenants',
    component: ApprenantManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'sop',
    component: SOPManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'clients',
    component: ClientManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'fournisseurs',
    component: FournisseurManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'projets',
    component: ProjetManagementNewComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'employees',
    component: EmployeeManagementComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: '**', 
    redirectTo: '/login' 
  }
];