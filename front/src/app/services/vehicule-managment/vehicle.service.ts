import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Vehicle } from '../../models/vehicule-mangment/vehicle.model';
import { Reparation } from '../../models/vehicule-mangment/reparation.model';
import { VehicleStats } from '../../models/vehicule-mangment/enum.model';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { environment } from '../../../environment/environement';
const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
const EXCEL_EXTENSION = '.xlsx';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
 

  private apiUrl = `${environment.apiUrl}/vehicule`;

  constructor(private http: HttpClient) { }


  getVehicles(): Observable<Vehicle[]> {
    return this.http.get<Vehicle[][]>(`${this.apiUrl}`).pipe(
      map(arr => arr[0]) 
    );
  }

  addVehicle(vehicle: Partial<Vehicle>): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.apiUrl}`, vehicle);
  }

  updateVehicle(id: string, updates: Partial<Vehicle>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteVehicle(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateKilometrage(id: string, nouveauKm: number): void {
    this.updateVehicle(id, { kmActuel: nouveauKm });
  }

  // getReparationsByVehicleId(vehicleId: string): Reparation[] {
  //   return this.reparationsSubject.value.filter(r => r.vehicleId === vehicleId);
  // }

  // addReparation(reparation: Omit<Reparation, 'id'>): void {
  //   const newReparation: Reparation = {
  //     ...reparation,
  //     id: Date.now().toString()
  //   };

  //   const currentReparations = this.reparationsSubject.value;
  //   this.reparationsSubject.next([...currentReparations, newReparation]);
  // }

  // updateReparation(id: string, updates: Partial<Reparation>): void {
  //   const currentReparations = this.reparationsSubject.value;
  //   const updatedReparations = currentReparations.map(reparation =>
  //     reparation.id === id
  //       ? { ...reparation, ...updates }
  //       : reparation
  //   );
  //   this.reparationsSubject.next(updatedReparations);
  // }

  // deleteReparation(id: string): void {
  //  // const currentReparations = this.reparationsSubject.value;
  //   const filteredReparations = currentReparations.filter(r => r.id !== id);
  //   this.reparationsSubject.next(filteredReparations);
  // }

  // Statistiques
  // getVehicleStats(): VehicleStats {
  //   const vehicles = this.vehiclesSubject.value;
  //   const reparations = this.reparationsSubject.value;
  //   const today = new Date();

  //   return {
  //     totalVehicles: vehicles.length,
  //     vehiculesEnPanne: 0, // À implémenter selon la logique métier
  //     prochainVidangeKm: vehicles.filter(v => v.kmActuel >= v.prochainVidangeKm).length,
  //     visiteTechniqueExpire: vehicles.filter(v => v.dateVisiteTechnique <= today).length,
  //     assuranceExpire: vehicles.filter(v => v.dateAssurance <= today).length,
  //     taxeExpire: vehicles.filter(v => v.dateTaxe <= today).length,
  //     totalReparations: reparations.length,
  //     coutTotalReparations: reparations.reduce((total, r) => total + r.prix, 0)
  //   };
  // }



  exportSelectedVehicle(selectedVehicle :Vehicle): void {
    if (! selectedVehicle) return;

    const v =  selectedVehicle;

    // --- Feuille 1 : Infos véhicule
    const vehicleSheetData = [{
      ID: v.id,
      Série: v.serie,
      Marque: v.marque,
      'Km Actuel': v.kmActuel,
      'Prochaine Vidange (km)': v.prochainVidangeKm,
      'Prochaine Chaine (km)': v.prochaineChaineKm,
      'Consommation (100 km)': v.consommation100km ?? '',
      'Date Visite Technique': v.dateVisiteTechnique ? this.formatDate(v.dateVisiteTechnique) : '',
      'Date Assurance': v.dateAssurance ? this.formatDate(v.dateAssurance) : '',
      'Date Taxe': v.dateTaxe ? this.formatDate(v.dateTaxe) : '',
      'Date Changement Batterie': v.dateChangementBatterie ? this.formatDate(v.dateChangementBatterie) : '',
      CreatedAt: v.createdAt ? this.formatDate(v.createdAt) : '',
      UpdatedAt: v.updatedAt ? this.formatDate(v.updatedAt) : ''
    }];

    // --- Feuille 2 : Réparations du véhicule
    const repairsSheetData = (v.reparations || []).map(r => ({
      'Réparation ID': r.id,
      'Type': String(r.type),
      'Prix (DT)': r.prix,
      'Date': r.date ? this.formatDate(r.date) : '',
      'Description': r.description || ''
    }));

    // Créer le workbook
    const wb = XLSX.utils.book_new();

    const wsVehicle = XLSX.utils.json_to_sheet(vehicleSheetData);
    XLSX.utils.book_append_sheet(wb, wsVehicle, 'Véhicule');

    const wsRepairs = XLSX.utils.json_to_sheet(repairsSheetData);
    XLSX.utils.book_append_sheet(wb, wsRepairs, 'Réparations');

    // Sauvegarder
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([wbout], { type: EXCEL_TYPE });
    saveAs(blob, `vehicule_${v.serie}_${new Date().toISOString().slice(0,10)}${EXCEL_EXTENSION}`);
  }

  private formatDate(d: Date | string): string {
    const date = new Date(d);
    if (isNaN(date.getTime())) return '';
    return date.toLocaleDateString('fr-FR'); // dd/mm/yyyy
  }
}