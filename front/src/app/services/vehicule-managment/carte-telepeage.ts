import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
 import * as XLSX from 'xlsx';
import { CarteTelepeage } from '../../models/vehicule-mangment/CarteTelepeage.model';
import { TelepeageTransaction } from '../../models/vehicule-mangment/TelepeageTransaction.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class CarteTelepeageService {
  private cartesSubject = new BehaviorSubject<CarteTelepeage[]>([]);
  public cartes$ = this.cartesSubject.asObservable();

  private transactionsSubject = new BehaviorSubject<TelepeageTransaction[]>([]);
  public transactions$ = this.transactionsSubject.asObservable();
 
  private apiUrl = `${environment.apiUrl}/carte-telepeage`;
  
  constructor(private http: HttpClient) {}

  // 🔹 Récupérer toutes les cartes
  getCartes(): Observable<CarteTelepeage[]> {   
     return this.http.get<CarteTelepeage[][]>(`${this.apiUrl}`)
        .pipe(
          map(arr => arr[0])  
        );
  }

  // 🔹 Ajouter une carte
  addCarte(carte: Partial<CarteTelepeage>): Observable<CarteTelepeage> {
    return this.http.post<CarteTelepeage>(this.apiUrl, carte);
  }

  // 🔹 Mettre à jour une carte
  updateCarte(id: string, updates: Partial<CarteTelepeage>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // 🔹 Supprimer une carte
  deleteCarte(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Transactions
  getTransactionsByCarteId(carteId: string): TelepeageTransaction[] {
    return this.transactionsSubject.value.filter(t => t.id === carteId);
  }

  addTransaction(transaction: Omit<TelepeageTransaction, 'id'>): void {
    const newTransaction: TelepeageTransaction = {
      ...transaction,
      id: Date.now().toString()
    };
    this.transactionsSubject.next([...this.transactionsSubject.value, newTransaction]);
  }

  deleteTransaction(id: string): void {
    const filtered = this.transactionsSubject.value.filter(t => t.id !== id);
    this.transactionsSubject.next(filtered);
  }

  // 🔹 Exporter carte + transactions en Excel
  exporteCarte(carte: CarteTelepeage) {
    if (!carte) return;

    const carteData = [{
      'Numéro Carte': carte.numero,
      'Fournisseur': carte.fournisseur,
      'Date d\'Émission': carte.dateEmission ? new Date(carte.dateEmission).toLocaleDateString() : '',
      'Solde (DT)': carte.solde,
      'Consommation (DT)': carte.consomation
    }];

    const transactionsData = carte.transactions?.map(t => ({
      'Date': t.date?.toLocaleDateString() || '',
      'Montant (DT)': t.montant,
      'Conducteur': t.conducteur || 'Non spécifié',
      'Description': t.description || ''
    })) || [];

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(carteData), 'Carte');
    XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(transactionsData), 'Transactions');
    XLSX.writeFile(wb, `CarteTelepeage_${carte.numero}.xlsx`);
  }
}
