export enum StatutEmployee {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  CONGE = 'CONGE',
  SUSPENDU = 'SUSPENDU'
}

export enum SituationFamiliale {
  CELIBATAIRE = 'CELIBATAIRE',
  MARIE = 'MARIE',
  DIVORCE = 'DIVORCE'   
}

export enum TypeConge {
  ANNUEL = 'ANNUEL',
  MALADIE = 'MALADIE',
  MATERNITE = 'MATERNITE',
  PATERNITE = 'PATERNITE',
  EXCEPTIONNEL = 'EXCEPTIONNEL',
  SANS_SOLDE = 'SANS_SOLDE'
}

export enum StatutConge {
  EN_ATTENTE = 'EN_ATTENTE',
  APPROUVE = 'APPROUVE',
  REFUSE = 'REFUSE',
  ANNULE = 'ANNULE'
}

export enum StatutFichePaie {
  BROUILLON = 'BROUILLON',
  VALIDE = 'VALIDE',
  PAYE = 'PAYE'
}

export enum TypePieceIdentite {
  CIN = 'CIN',
  PASSEPORT = 'PASSEPORT',
 }