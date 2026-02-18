import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import {  HRStats  } from '../models/rh/HRStats.model';
import { Employee } from '../models/rh/Employee.model';
import { Congee } from '../models/rh/Congee.model';
import { FichePaie } from '../models/rh/FichePaie.model';
import { Prime } from '../models/rh/Prime.model';
import { Retenue } from '../models/rh/Retenue.model';
import { Regle, TypeRegle, StatutRegle, RegleStats } from '../models/rh/Regle.model';
import { SituationFamiliale, StatutConge, StatutEmployee, StatutFichePaie, TypeConge, TypePieceIdentite } from '../models/rh/enum.model';

@Injectable({
  providedIn: 'root'
})
export class HRService {
  private employeesSubject = new BehaviorSubject<Employee[]>([]);
  public employees$ = this.employeesSubject.asObservable();

  private congesSubject = new BehaviorSubject<Congee[]>([]);
  public conges$ = this.congesSubject.asObservable();

  private fichesSubject = new BehaviorSubject<FichePaie[]>([]);
  public fiches$ = this.fichesSubject.asObservable();

  private primesSubject = new BehaviorSubject<Prime[]>([]);
  public primes$ = this.primesSubject.asObservable();

  private retenuesSubject = new BehaviorSubject<Retenue[]>([]);
  public retenues$ = this.retenuesSubject.asObservable();

  private reglesSubject = new BehaviorSubject<Regle[]>([]);
  public regles$ = this.reglesSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockEmployees: Employee[] = [
      {
        id: '1',
        nom: 'Ben Ahmed',
        prenom: 'Mohamed',
        email: 'mohamed@sms2i.com',
        telephone: '+216 20 123 456',
        poste: 'Développeur Senior',
        departement: 'IT',
        dateEmbauche: new Date('2023-01-15'),
        salaire: 2500,
        soldeConges: 25,
        congesUtilises: 5,
        soldePoints: 120,
        pointsDemandesParAn: 50,
        adresse: '123 Avenue Habib Bourguiba, Tunis',
        typePieceIdentite: TypePieceIdentite.CIN,
        numeroPieceIdentite: '12345678',
        nombreEnfants: 2,
        statut: StatutEmployee.ACTIF,
        conges: [],
        fichesPaie: [],
        retenues: [],
        primes: [],
        createdAt: new Date('2023-01-15'),
        updatedAt: new Date('2024-01-20')
      },
      {
        id: '2',
        nom: 'Trabelsi',
        prenom: 'Fatma',
        email: 'fatma@sms2i.com',
        telephone: '+216 25 987 654',
        poste: 'Chef de Projet',
        departement: 'Management',
        dateEmbauche: new Date('2023-03-01'),
        salaire: 3000,
        soldeConges: 25,
        congesUtilises: 8,
        soldePoints: 95,
        pointsDemandesParAn: 40,
        adresse: '456 Rue de la République, Sfax',
        typePieceIdentite: TypePieceIdentite.CIN,
        numeroPieceIdentite: '87654321',
        nombreEnfants: 1,
        statut: StatutEmployee.ACTIF,
        conges: [],
        fichesPaie: [],
        retenues: [],
        primes: [],
        createdAt: new Date('2023-03-01'),
        updatedAt: new Date('2024-01-15')
      }
    ];

    const mockConges: Congee[] = [
      {
        id: '1',
        type: TypeConge.ANNUEL,
        dateDebut: new Date('2024-02-15'),
        dateFin: new Date('2024-02-20'),
        nombreJours: 5,
        motif: 'Congés annuels',
        statut: StatutConge.APPROUVE,
        dateValidation: new Date('2024-02-10'),
        validePar: 'admin',
        employee: mockEmployees[0]
      }
    ];

    const mockFiches: FichePaie[] = [
      {
        id: '1',
        dateEmission: new Date('2024-01-31'),
        salaireDeBase: 2500,
        netAPayer: 2280,
        statut: StatutFichePaie.VALIDE,
        employee: mockEmployees[0],
        primes: [],
        retenues: []
      }
    ];

    const mockPrimes: Prime[] = [
      {
        id: '1',
        libelle: 'Prime de performance',
        montant: 500,
        nombrePoints: 10,
        description: 'Prime pour excellent travail sur le projet X',
        fichePaie: mockFiches[0],
        employee: mockEmployees[0],
        createdAt: new Date('2024-01-25'),
        updatedAt: new Date('2024-01-25')
      }
    ];

    const mockRetenues: Retenue[] = [
      {
        id: '1',
        libelle: 'Retenue absence',
        montant: 100,
        nombrePoints: 5,
        description: 'Retenue pour absence non justifiée',
        fichePaie: mockFiches[0],
        employee: mockEmployees[0],
        createdAt: new Date('2024-01-20'),
        updatedAt: new Date('2024-01-20')
      }
    ];

    this.employeesSubject.next(mockEmployees);
    this.congesSubject.next(mockConges);
    this.fichesSubject.next(mockFiches);
    this.primesSubject.next(mockPrimes);
    this.retenuesSubject.next(mockRetenues);
    this.loadMockRegles();
  }

  private loadMockRegles(): void {
    const mockRegles: Regle[] = [
      {
        id: '1',
        libelle: 'Prime d\'ancienneté',
        description: 'Prime accordée selon l\'ancienneté de l\'employé',
        minAnciennete: 2,
        maxAnciennete: 99,
        pourcentageSalaire: 0.05, // 5% du salaire
        nombrePoints: 10,
        typeRegle: TypeRegle.PRIME,
        statut: StatutRegle.ACTIVE,
        dateCreation: new Date('2024-01-01'),
        dateModification: new Date('2024-01-01'),
        conditions: 'Employé avec plus de 2 ans d\'ancienneté',
        automatique: true,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      },
      {
        id: '2',
        libelle: 'Prime de performance',
        description: 'Prime basée sur les performances annuelles',
        minAnciennete: 1,
        maxAnciennete: 99,
        montantFixe: 500,
        nombrePoints: 15,
        typeRegle: TypeRegle.PRIME,
        statut: StatutRegle.ACTIVE,
        dateCreation: new Date('2024-01-01'),
        dateModification: new Date('2024-01-01'),
        conditions: 'Évaluation annuelle supérieure à 4/5',
        automatique: false,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      },
      {
        id: '3',
        libelle: 'Retenue absence injustifiée',
        description: 'Retenue appliquée pour absence non justifiée',
        minAnciennete: 0,
        maxAnciennete: 99,
        pourcentageSalaire: 0.02, // 2% du salaire par jour
        nombrePoints: -5,
        typeRegle: TypeRegle.RETENUE,
        statut: StatutRegle.ACTIVE,
        dateCreation: new Date('2024-01-01'),
        dateModification: new Date('2024-01-01'),
        conditions: 'Absence non justifiée par jour',
        automatique: true,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      },
      {
        id: '4',
        libelle: 'Prime de transport',
        description: 'Prime de transport pour les employés éloignés',
        minAnciennete: 0,
        maxAnciennete: 99,
        montantFixe: 100,
        nombreJours: 0,
        nombrePoints: 5,
        typeRegle: TypeRegle.PRIME,
        statut: StatutRegle.ACTIVE,
        dateCreation: new Date('2024-01-01'),
        dateModification: new Date('2024-01-01'),
        conditions: 'Domicile à plus de 20km du bureau',
        automatique: false,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      }
    ];

    this.reglesSubject.next(mockRegles);
  }

  // Gestion des employés
  getEmployees(): Observable<Employee[]> {
    return this.employees$;
  }

  getEmployeeById(id: string): Employee | undefined {
    return this.employeesSubject.value.find(e => e.id === id);
  }

  addEmployee(employee: Omit<Employee, 'id' | 'createdAt' | 'updatedAt'>): void {
    const newEmployee: Employee = {
      ...employee,
      id: Date.now().toString(),
      createdAt: new Date(),
      updatedAt: new Date(),
      conges: [],
      fichesPaie: [],
      retenues: [],
      primes: [],
      soldePoints: 100,
      pointsDemandesParAn: 25,
      typePieceIdentite: TypePieceIdentite.CIN
    };

    const currentEmployees = this.employeesSubject.value;
    this.employeesSubject.next([...currentEmployees, newEmployee]);
  }

  updateEmployee(id: string, updates: Partial<Employee>): void {
    const currentEmployees = this.employeesSubject.value;
    const updatedEmployees = currentEmployees.map(employee => 
      employee.id === id 
        ? { ...employee, ...updates, updatedAt: new Date() }
        : employee
    );
    this.employeesSubject.next(updatedEmployees);
  }

  deleteEmployee(id: string): void {
    const currentEmployees = this.employeesSubject.value;
    const filteredEmployees = currentEmployees.filter(e => e.id !== id);
    this.employeesSubject.next(filteredEmployees);

    // Supprimer aussi les congés et fiches associés
    const currentConges = this.congesSubject.value;
    const filteredConges = currentConges.filter(c => c.employee.id !== id);
    this.congesSubject.next(filteredConges);

    const currentFiches = this.fichesSubject.value;
    const filteredFiches = currentFiches.filter(f => f.employee.id !== id);
    this.fichesSubject.next(filteredFiches);
  }

  // Gestion des congés
  getConges(): Observable<Congee[]> {
    return this.conges$;
  }

  getCongesByEmployeeId(employeeId: string): Congee[] {
    return this.congesSubject.value.filter(c => c.employee.id === employeeId);
  }

  addConge(conge: Omit<Congee, 'id'>): void {
    const newConge: Congee = {
      id: Date.now().toString(),
      ...conge,
    } as Congee;

    const currentConges = this.congesSubject.value;
    this.congesSubject.next([...currentConges, newConge]);
  }

  updateConge(id: string, updates: Partial<Congee>): void {
    const currentConges = this.congesSubject.value;
    const updatedConges = currentConges.map(conge => 
      conge.id === id ? { ...conge, ...updates } : conge
    );
    this.congesSubject.next(updatedConges);
  }

  approveConge(id: string, validePar: string): void {
    this.updateConge(id, {
      statut: StatutConge.APPROUVE,
      dateValidation: new Date(),
      validePar: validePar
    });

    // Déduire du solde de congés
    const conge = this.congesSubject.value.find(c => c.id === id);
    if (conge) {
      const employee = this.getEmployeeById(conge.employee.id);
      if (employee) {
        this.updateEmployee(employee.id, {
          congesUtilises: employee.congesUtilises + conge.nombreJours,
          soldeConges: employee.soldeConges - conge.nombreJours
        });
      }
    }
  }

  // Gestion des fiches de paie
  getFiches(): Observable<FichePaie[]> {
    return this.fiches$;
  }

  getFichesByEmployeeId(employeeId: string): FichePaie[] {
    return this.fichesSubject.value.filter(f => f.employee.id === employeeId);
  }

  addFiche(fiche: Omit<FichePaie, 'id'>): void {
    const newFiche: FichePaie = {
      ...fiche,
      id: Date.now().toString()
    };

    const currentFiches = this.fichesSubject.value;
    this.fichesSubject.next([...currentFiches, newFiche]);
  }

  updateFiche(id: string, updates: Partial<FichePaie>): void {
    const currentFiches = this.fichesSubject.value;
    const updatedFiches = currentFiches.map(fiche => 
      fiche.id === id ? { ...fiche, ...updates } : fiche
    );
    this.fichesSubject.next(updatedFiches);
  }

  deleteFiche(id: string): void {
    const currentFiches = this.fichesSubject.value;
    const filteredFiches = currentFiches.filter(f => f.id !== id);
    this.fichesSubject.next(filteredFiches);
  }

  // Gestion des primes
  getPrimes(): Observable<Prime[]> {
    return this.primes$;
  }

  getPrimesByEmployeeId(employeeId: string): Prime[] {
    return this.primesSubject.value.filter(p => p.employee.id === employeeId);
  }

  addPrime(prime: Omit<Prime, 'id' | 'createdAt' | 'updatedAt' | 'fichePaie'>): void {
    const newPrime: Prime = {
      ...prime,
      id: Date.now().toString(),
      fichePaie: {} as FichePaie, // À associer lors de l'ajout à une fiche
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentPrimes = this.primesSubject.value;
    this.primesSubject.next([...currentPrimes, newPrime]);
  }

  updatePrime(id: string, updates: Partial<Prime>): void {
    const currentPrimes = this.primesSubject.value;
    const updatedPrimes = currentPrimes.map(prime => 
      prime.id === id ? { ...prime, ...updates, updatedAt: new Date() } : prime
    );
    this.primesSubject.next(updatedPrimes);
  }

  deletePrime(id: string): void {
    const currentPrimes = this.primesSubject.value;
    const filteredPrimes = currentPrimes.filter(p => p.id !== id);
    this.primesSubject.next(filteredPrimes);
  }

  // Gestion des retenues
  getRetenues(): Observable<Retenue[]> {
    return this.retenues$;
  }

  getRetenuesByEmployeeId(employeeId: string): Retenue[] {
    return this.retenuesSubject.value.filter(r => r.employee.id === employeeId);
  }

  addRetenue(retenue: Omit<Retenue, 'id' | 'createdAt' | 'updatedAt' | 'fichePaie'>): void {
    const newRetenue: Retenue = {
      ...retenue,
      id: Date.now().toString(),
      fichePaie: {} as FichePaie, // À associer lors de l'ajout à une fiche
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentRetenues = this.retenuesSubject.value;
    this.retenuesSubject.next([...currentRetenues, newRetenue]);
  }

  updateRetenue(id: string, updates: Partial<Retenue>): void {
    const currentRetenues = this.retenuesSubject.value;
    const updatedRetenues = currentRetenues.map(retenue => 
      retenue.id === id ? { ...retenue, ...updates, updatedAt: new Date() } : retenue
    );
    this.retenuesSubject.next(updatedRetenues);
  }

  deleteRetenue(id: string): void {
    const currentRetenues = this.retenuesSubject.value;
    const filteredRetenues = currentRetenues.filter(r => r.id !== id);
    this.retenuesSubject.next(filteredRetenues);
  }

  // Gestion des règles
  getRegles(): Observable<Regle[]> {
    return this.regles$;
  }

  getRegleById(id: string): Regle | undefined {
    return this.reglesSubject.value.find(r => r.id === id);
  }

  addRegle(regle: Omit<Regle, 'id' | 'createdAt' | 'updatedAt'>): void {
    const newRegle: Regle = {
      ...regle,
      id: Date.now().toString(),
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentRegles = this.reglesSubject.value;
    this.reglesSubject.next([...currentRegles, newRegle]);
  }

  updateRegle(id: string, updates: Partial<Regle>): void {
    const currentRegles = this.reglesSubject.value;
    const updatedRegles = currentRegles.map(regle => 
      regle.id === id 
        ? { ...regle, ...updates, updatedAt: new Date() }
        : regle
    );
    this.reglesSubject.next(updatedRegles);
  }

  deleteRegle(id: string): void {
    const currentRegles = this.reglesSubject.value;
    const filteredRegles = currentRegles.filter(r => r.id !== id);
    this.reglesSubject.next(filteredRegles);
  }

  getReglesActives(): Regle[] {
    return this.reglesSubject.value.filter(r => r.statut === StatutRegle.ACTIVE);
  }

  getReglesApplicables(employeeId: string): Regle[] {
    const employee = this.getEmployeeById(employeeId);
    if (!employee) return [];

    const anciennete = this.calculateAnciennete(employee.dateEmbauche);
    
    return this.getReglesActives().filter(regle => 
      anciennete >= regle.minAnciennete && anciennete <= regle.maxAnciennete
    );
  }

  appliquerRegle(employeeId: string, regleId: string): void {
    const regle = this.getRegleById(regleId);
    const employee = this.getEmployeeById(employeeId);
    
    if (!regle || !employee) return;

    if (regle.typeRegle === TypeRegle.PRIME) {
      const montant = this.calculateMontantRegle(regle, employee.salaire);
      const primeData: Omit<Prime, 'id' | 'createdAt' | 'updatedAt' | 'fichePaie'> = {
        libelle: regle.libelle,
        montant: montant,
        nombrePoints: regle.nombrePoints || 0,
        description: `Prime appliquée selon règle: ${regle.description}`,
        employee: employee
      };
      this.addPrime(primeData);
    } else {
      const montant = this.calculateMontantRegle(regle, employee.salaire);
      const retenueData: Omit<Retenue, 'id' | 'createdAt' | 'updatedAt' | 'fichePaie'> = {
        libelle: regle.libelle,
        montant: montant,
        nombrePoints: regle.nombrePoints || 0,
        description: `Retenue appliquée selon règle: ${regle.description}`,
        employee: employee
      };
      this.addRetenue(retenueData);
    }
  }

  private calculateMontantRegle(regle: Regle, salaire: number): number {
    if (regle.montantFixe) {
      return regle.montantFixe;
    } else if (regle.pourcentageSalaire) {
      return salaire * regle.pourcentageSalaire;
    }
    return 0;
  }

  private calculateAnciennete(dateEmbauche: Date): number {
    const today = new Date();
    const embauche = new Date(dateEmbauche);
    const diffTime = Math.abs(today.getTime() - embauche.getTime());
    const diffYears = Math.floor(diffTime / (1000 * 60 * 60 * 24 * 365.25));
    return diffYears;
  }

  getRegleStats(): RegleStats {
    const regles = this.reglesSubject.value;
    const employees = this.employeesSubject.value;
    
    const reglesActives = regles.filter(r => r.statut === StatutRegle.ACTIVE).length;
    const reglesInactives = regles.filter(r => r.statut === StatutRegle.INACTIVE).length;
    const primes = regles.filter(r => r.typeRegle === TypeRegle.PRIME).length;
    const retenues = regles.filter(r => r.typeRegle === TypeRegle.RETENUE).length;
    const reglesAutomatiques = regles.filter(r => r.automatique).length;
    
    // Calcul des montants totaux potentiels
    let montantTotalPrimes = 0;
    let montantTotalRetenues = 0;
    let employesConcernes = 0;
    
    employees.forEach(employee => {
      const reglesApplicables = this.getReglesApplicables(employee.id);
      if (reglesApplicables.length > 0) {
        employesConcernes++;
      }
      
      reglesApplicables.forEach(regle => {
        const montant = this.calculateMontantRegle(regle, employee.salaire);
        if (regle.typeRegle === TypeRegle.PRIME) {
          montantTotalPrimes += montant;
        } else {
          montantTotalRetenues += montant;
        }
      });
    });

    return {
      totalRegles: regles.length,
      reglesActives,
      reglesInactives,
      primes,
      retenues,
      reglesAutomatiques,
      employesConcernes,
      montantTotalPrimes,
      montantTotalRetenues
    };
  }

  // Statistiques
  getHRStats(): HRStats {
    const employees = this.employeesSubject.value;
    const conges = this.congesSubject.value;
    const fiches = this.fichesSubject.value;

    const employeesActifs = employees.filter(e => e.statut === StatutEmployee.ACTIF);
    const employeesEnConge = employees.filter(e => e.statut === StatutEmployee.CONGE);
    const congesEnAttente = conges.filter(c => c.statut === StatutConge.EN_ATTENTE);
    const fichesNonPayees = fiches.filter(f => f.statut !== StatutFichePaie.PAYE);

    const masseSalariale = employees.reduce((total, e) => total + e.salaire, 0);
    const moyenneCongesUtilises = employees.length > 0 
      ? employees.reduce((total, e) => total + e.congesUtilises, 0) / employees.length 
      : 0;

    return {
      totalEmployees: employees.length,
      employeesActifs: employeesActifs.length,
      employeesEnConge: employeesEnConge.length,
      congesEnAttente: congesEnAttente.length,
      masseSalariale: masseSalariale,
      fichesNonPayees: fichesNonPayees.length,
      moyenneCongesUtilises: Math.round(moyenneCongesUtilises * 10) / 10,
      turnoverRate: 0 // À calculer selon la logique métier
    };
  }

  // Utilitaires
  generateMatricule(): string {
    const employees = this.employeesSubject.value;
    const lastMatricule = employees.length > 0 
      ? Math.max(...employees.map(e => parseInt(e.id.replace('EMP', '')))) 
      : 0;
    return `EMP${String(lastMatricule + 1).padStart(3, '0')}`;
  }

  calculateSalaireNet(salaireBrut: number, retenues: number, cotisations: number, impots: number): number {
    return salaireBrut - retenues - cotisations - impots;
  }
}