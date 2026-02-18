import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, Router, RouterOutlet, withInMemoryScrolling } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OnInit, inject } from '@angular/core';
import { AuthService } from './app/services/auth.service';
import { routes } from './app/app.routes';
import { HRService } from './app/services/hr.service';
import { StockService } from './app/services/stock.service';
import { PretService } from './app/services/pret.service';
import { SousTraitanceService } from './app/services/sous-traitance.service';
import { ProjetService } from './app/services/projet.service';
import { SOPService } from './app/services/sop.service';
import { MarketingService } from './app/services/marketing.service';
import { ImmobilierService } from './app/services/immobilier.service';
import { FournisseurService } from './app/services/fournisseur.service';
import { ClientService } from './app/services/client.service';
import { CarteGazoilService } from './app/services/vehicule-managment/carte-gazoil.service';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { VehicleService } from './app/services/vehicule-managment/vehicle.service';
import { ProjetManagementService } from './app/services/projet-management.service';
import { AuthGuard } from './app/guards/auth.guard';
import { authInterceptor } from './app/interceptors/auth.interceptor';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `<router-outlet></router-outlet>`
})
export class App implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    // Don't do automatic redirection here - let AuthGuard handle protected routes
    // This prevents race conditions with route initialization
    const isAuthenticated = this.authService.isAuthenticated();
    const currentPath = window.location.pathname;
    
    console.log('🚀 App init - isAuthenticated:', isAuthenticated, 'currentPath:', currentPath);
    
    // Only redirect to menu if on root or login page AND authenticated
    if (isAuthenticated && (currentPath === '/' || currentPath === '/login')) {
      this.router.navigate(['/menu']);
    }
    // Do NOT redirect unauthenticated users here - let AuthGuard handle it
    // This prevents issues when navigating directly to protected routes
  }
}

bootstrapApplication(App, {
  providers: [ 
    provideHttpClient(withInterceptors([authInterceptor])), 
    provideRouter(routes),
    provideAnimationsAsync(),
    AuthService,
    AuthGuard,
    VehicleService,
    HRService,
    StockService,
    PretService,
    SousTraitanceService,
    ProjetService,
    ProjetManagementService,
    SOPService,
    MarketingService,
    ImmobilierService,
    FournisseurService,
    ClientService,
    CarteGazoilService,
  ]
});