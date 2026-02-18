import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { TransactionCarburant } from '../../models/vehicule-mangment/transaction.model';
import { CarteGazoil } from '../../models/vehicule-mangment/carte-gazoil.model';
import { CarteGazoilStats, StatutCarte } from '../../models/vehicule-mangment/enum.model';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { environment } from '../../../environment/environement';
const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
const EXCEL_EXTENSION = '.xlsx';
@Injectable({
  providedIn: 'root'
})
export class CarteGazoilService {
  private cartesSubject = new BehaviorSubject<CarteGazoil[]>([]);
  public cartes$ = this.cartesSubject.asObservable();

  private transactionsSubject = new BehaviorSubject<TransactionCarburant[]>([]);
  public transactions$ = this.transactionsSubject.asObservable();


  private apiUrl = `${environment.apiUrl}/carte-gazoil`;
  
  constructor(private http: HttpClient) { }

  // 🔹 Récupérer toutes les cartes
  getCartes(): Observable<CarteGazoil[]> {
    return this.http.get<CarteGazoil[][]>(`${this.apiUrl}`)
      .pipe(
        map(arr => arr[0])
      );
  }

  // 🔹 Ajouter une carte
  addCarte(carte: Partial<CarteGazoil>): Observable<CarteGazoil> {
    return this.http.post<CarteGazoil>(`${this.apiUrl}`, carte);
  }

  // 🔹 Mettre à jour une carte
  updateCarte(id: string, updates: Partial<CarteGazoil>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // 🔹 Supprimer une carte
  deleteCarte(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Rechercher (filtrer)
  searchCartes(query: any): Observable<CarteGazoil[]> {
    return this.http.post<CarteGazoil[]>(`${this.apiUrl}/search`, query);
  }

  getCarteById(id: string): CarteGazoil | undefined {
    return this.cartesSubject.value.find(c => c.id === id);
  }



  // Gestion des transactions
  getTransactions(): Observable<TransactionCarburant[]> {
    return this.transactions$;
  }

  getTransactionsByCarteId(carteId: string): TransactionCarburant[] {
    return this.transactionsSubject.value.filter(t => t.carteId === carteId);
  }

  addTransaction(transaction: Omit<TransactionCarburant, 'id'>): void {
    const newTransaction: TransactionCarburant = {
      ...transaction,
      id: Date.now().toString()
    };

    const currentTransactions = this.transactionsSubject.value;
    this.transactionsSubject.next([...currentTransactions, newTransaction]);


  }

  updateTransaction(id: string, updates: Partial<TransactionCarburant>): void {
    const currentTransactions = this.transactionsSubject.value;
    const updatedTransactions = currentTransactions.map(transaction =>
      transaction.id === id ? { ...transaction, ...updates } : transaction
    );
    this.transactionsSubject.next(updatedTransactions);
  }

  deleteTransaction(id: string): void {
    const transaction = this.transactionsSubject.value.find(t => t.id === id);
    const currentTransactions = this.transactionsSubject.value;
    const filteredTransactions = currentTransactions.filter(t => t.id !== id);
    this.transactionsSubject.next(filteredTransactions);


  }


  generateNumeroCarte(): string {
    const cartes = this.cartesSubject.value;
    const currentYear = new Date().getFullYear();
    const lastNumber = cartes.length > 0
      ? Math.max(...cartes.map(c => {
        const match = c.numero.match(/CG-(\d+)-/);
        return match ? parseInt(match[1]) : 0;
      }))
      : 0;
    return `CG-${String(lastNumber + 1).padStart(3, '0')}-${currentYear}`;
  }

  exporteCarte(carte: CarteGazoil) {
    if (!carte) return;

    // Préparer les données de la carte
    const carteData = [
      {
        'Numéro Carte': carte.numero,
        'Fournisseur': carte.fournisseur, // selon ta structure
        'Date d\'Émission': carte.dateEmission ? new Date(carte.dateEmission).toLocaleDateString() : '',
        'Solde (DT)': carte.solde,
        'Consommation (DT)': carte.consomation
      }
    ];

    // Préparer les données des transactions
    const transactionsData = carte.transactions.map(t => ({
      'Date': t.date ? new Date(t.date).toLocaleDateString() : '',
      'Station': t.station,
      'Adresse Station': t.adresseStation,
      'Quantité (L)': t.quantite,
      'Prix/Litre': t.prixLitre,
      'Montant Total (DT)': t.montantTotal,
      'Kilométrage': t.kilometrage,
      'Conducteur': t.conducteur || 'Non spécifié',
      'Type Carburant': t.typeCarburant // selon ta structure
    }));

    // Créer un workbook
    const wb = XLSX.utils.book_new();

    // Ajouter feuille Carte
    const wsCarte = XLSX.utils.json_to_sheet(carteData);
    XLSX.utils.book_append_sheet(wb, wsCarte, 'Carte');

    // Ajouter feuille Transactions
    const wsTransactions = XLSX.utils.json_to_sheet(transactionsData);
    XLSX.utils.book_append_sheet(wb, wsTransactions, 'Transactions');

    // Exporter le fichier
    XLSX.writeFile(wb, `Carte_${carte.numero}.xlsx`);
  }
}