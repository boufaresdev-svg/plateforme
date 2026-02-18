import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { StockService } from '../../../../services/stock.service';
import { ArticleService } from '../../../../services/article.service';
import { CategoryService } from '../../../../services/category.service';
import { MarqueService } from '../../../../services/marque.service';
import { EntrepotService } from '../../../../services/entrepot.service';
import { AlertService } from '../../../../services/alert.service';
import { StockTraceService } from '../../../../services/stock-trace.service';
import { StockLotService } from '../../../../services/stock-lot.service';
import { StockStats, StockLot } from '../../../../models/stock.model';
import { Alerte } from '../../../../models/alert.model';
import { StockTraceStats } from '../../../../models/stock-trace.model';

@Component({
  selector: 'app-stock-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-dashboard.component.html',
  styleUrls: ['./stock-dashboard.component.css']
})
export class StockDashboardComponent implements OnInit {
  loading = true;
  
  // Stats
  totalArticles = 0;
  totalCategories = 0;
  totalMarques = 0;
  totalEntrepots = 0;
  totalStockValue = 0;
  articlesDisponibles = 0;
  articlesRupture = 0;
  
  // Alerts
  alertes: Alerte[] = [];
  alertesNonLues = 0;
  
  // Movements
  mouvementStats?: StockTraceStats;
  recentMovementsCount = 0;
  
  // Categories distribution
  categoriesStats: any[] = [];
  
  // Stock by warehouse
  stockParEntrepot: any[] = [];
  
  // Low stock items
  articlesStockFaible: any[] = [];

  private dataLoaded = false;

  constructor(
    private stockService: StockService,
    private articleService: ArticleService,
    private categoryService: CategoryService,
    private marqueService: MarqueService,
    private entrepotService: EntrepotService,
    private alertService: AlertService,
    private stockTraceService: StockTraceService,
    private stockLotService: StockLotService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Delay loading to avoid blocking initial render
    // Load data only after view is initialized
    setTimeout(() => {
      if (!this.dataLoaded) {
        this.loadDashboardData();
        this.dataLoaded = true;
      }
    }, 100);
  }

  loadDashboardData(): void {
    this.loading = true;
    
    forkJoin({
      articles: this.articleService.getArticles({ page: 0, size: 20 }), // Reduced: only need for stats
      categories: this.categoryService.getCategories(),
      marques: this.marqueService.getMarques(),
      entrepots: this.entrepotService.getEntrepots(),
      stocks: this.stockService.getAllStocks({ page: 0, size: 20 }), // Reduced: only need for value calculation
      stockLots: this.stockLotService.getStockLots({ estActif: true }), // Get all active lots for value calculation
      alertes: this.alertService.getActiveAlertes(),
      mouvementStats: this.stockTraceService.getStockTraceStats()
    }).subscribe({
      next: (data) => {
        // Extract content from paginated responses
        const articles = data.articles.content || [];
        const stocks = data.stocks.content || [];
        const stockLots = data.stockLots || [];
        
        // Basic counts - use totalElements from API response, not array length
        this.totalArticles = data.articles.totalElements || articles.length;
        this.totalCategories = data.categories.length;
        this.totalMarques = data.marques.length;
        this.totalEntrepots = data.entrepots.length;
        
        // Stock stats - use sample for calculations
        this.articlesDisponibles = articles.filter(a => a.estActif).length;
        // Calculate total stock value from lots (using purchase price * quantity)
        this.totalStockValue = this.calculateTotalStockValueFromLots(stockLots);
        
        // Alerts
        this.alertes = data.alertes.slice(0, 5); // Top 5 alerts
        this.alertesNonLues = data.alertes.filter((a: Alerte) => !a.isRead).length;
        this.articlesRupture = data.alertes.filter((a: Alerte) => a.type === 'RUPTURE').length;
        
        // Movement stats
        this.mouvementStats = data.mouvementStats;
        this.recentMovementsCount = data.mouvementStats.totalActions;
        
        // Categories distribution
        this.categoriesStats = this.calculateCategoriesDistribution(articles, data.categories);
        
        // Stock by warehouse - use lots for value calculation
        this.stockParEntrepot = this.calculateStockByWarehouseFromLots(stocks, data.entrepots, stockLots);
        
        // Low stock items
        this.articlesStockFaible = data.alertes
          .filter((a: Alerte) => a.type === 'STOCK_FAIBLE')
          .slice(0, 5);
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.loading = false;
      }
    });
  }

  private calculateTotalStockValueFromLots(lots: StockLot[]): number {
    // Calculate total stock value from all active lots (using purchase price * quantity)
    return lots.reduce((total, lot) => {
      const value = (lot.quantiteActuelle || 0) * (lot.prixAchatUnitaire || 0);
      return total + value;
    }, 0);
  }

  private calculateCategoriesDistribution(articles: any[], categories: any[]): any[] {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'];
    
    const distribution = categories.map((cat, index) => {
      const count = articles.filter(a => a.categorieId === cat.id).length;
      return {
        id: cat.id,
        nom: cat.nom,
        nombre: count,
        percentage: this.totalArticles > 0 ? (count / this.totalArticles) * 100 : 0,
        couleur: colors[index % colors.length]
      };
    });
    
    return distribution.filter(d => d.nombre > 0).sort((a, b) => b.nombre - a.nombre);
  }

  private calculateStockByWarehouseFromLots(stocks: any[], entrepots: any[], lots: StockLot[]): any[] {
    return entrepots.map(entrepot => {
      const warehouseStocks = stocks.filter(s => s.entrepotId === entrepot.id);
      const warehouseLots = lots.filter(lot => lot.entrepotId === entrepot.id);
      
      // Calculate total quantity from lots
      const totalQuantity = warehouseLots.reduce((sum, lot) => sum + (lot.quantiteActuelle || 0), 0);
      // Calculate total value from lots (using purchase price)
      const totalValue = warehouseLots.reduce((sum, lot) => 
        sum + ((lot.quantiteActuelle || 0) * (lot.prixAchatUnitaire || 0)), 0
      );
      
      return {
        id: entrepot.id,
        nom: entrepot.nom,
        adresse: entrepot.adresse,
        articles: warehouseStocks.length,
        quantite: totalQuantity,
        valeur: totalValue
      };
    }).filter(w => w.articles > 0 || w.quantite > 0);
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-TN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount) + ' DT';
  }

  formatNumber(num: number): string {
    return new Intl.NumberFormat('fr-FR').format(num);
  }

  getAlertClass(alerte: Alerte): string {
    switch (alerte.priority) {
      case 'CRITICAL': return 'danger';
      case 'HIGH': return 'warning';
      case 'MEDIUM': return 'info';
      default: return 'success';
    }
  }

  getAlertIcon(alerte: Alerte): string {
    switch (alerte.type) {
      case 'RUPTURE': return '❌';
      case 'STOCK_FAIBLE': return '⚠️';
      case 'STOCK_ELEVE': return '📈';
      default: return '🔔';
    }
  }

  voirArticle(articleId: string): void {
    // Navigate to article details or stock view
  }

  voirAlerte(alerte: Alerte): void {
    this.alertService.markAsRead(alerte.id).subscribe(() => {
      this.loadDashboardData();
    });
  }

  voirMouvements(): void {
    // Navigate to movements view
  }

  voirToutesAlertes(): void {
    // Navigate to alerts view
  }

  refreshDashboard(): void {
    this.loadDashboardData();
  }
}