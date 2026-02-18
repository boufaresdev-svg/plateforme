import { Injectable } from '@angular/core';
import { Formation } from '../../models/formation/formation.model';
import { PlanFormation } from '../../models/formation/PlanFormation.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class PdfGeneratorService {

  constructor() { }

  // Generate PDF without content links (simple version)
  generateFormationProgramPdf(formation: Formation, plan: PlanFormation): void {
    const htmlContent = this.generateHtmlContent(formation, plan, false);
    this.openPdfWindow(htmlContent);
  }

  // Generate PDF with content links (advanced version)
  generateFormationProgramPdfWithLinks(formation: Formation, plan: PlanFormation): void {
    const htmlContent = this.generateHtmlContent(formation, plan, true);
    this.openPdfWindow(htmlContent);
  }

  // Download complete package with PDF and content files
  async downloadFormationPackage(formation: Formation, plan: PlanFormation): Promise<void> {
    alert('La génération du package complet avec tous les contenus est en cours. Cette fonctionnalité nécessite un backend pour zipper les fichiers.');
    // This would require backend support to create a ZIP file with:
    // 1. The PDF program
    // 2. All content files from formation.contenusFormation
    // For now, we'll generate the PDF with links
    this.generateFormationProgramPdfWithLinks(formation, plan);
  }

  private openPdfWindow(htmlContent: string): void {
    const printWindow = window.open('', '_blank');
    if (!printWindow) {
      alert('Veuillez autoriser les popups pour télécharger le PDF');
      return;
    }

    printWindow.document.write(htmlContent);
    printWindow.document.close();
    
    // Wait for content to load then print
    setTimeout(() => {
      printWindow.print();
    }, 750);
  }

  // Helper method to clean HTML from text content
  private cleanHtmlContent(text: string): string {
    if (!text) return '';
    
    // Create a temporary div to parse HTML
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = text;
    
    // Get text content (this strips all HTML tags)
    let cleanText = tempDiv.textContent || tempDiv.innerText || '';
    
    // Trim and remove extra whitespace
    cleanText = cleanText.trim().replace(/\s+/g, ' ');
    
    // Remove any remaining HTML entities
    cleanText = cleanText.replace(/&nbsp;/g, ' ')
                         .replace(/&amp;/g, '&')
                         .replace(/&lt;/g, '<')
                         .replace(/&gt;/g, '>')
                         .replace(/&quot;/g, '"')
                         .replace(/&#39;/g, "'");
    
    return cleanText;
  }

  private generateHtmlContent(formation: Formation, plan: PlanFormation, includeLinks: boolean = false): string {
    const today = new Date().toLocaleDateString('fr-FR');
    const refNumber = `FORM.${String(plan.idPlanFormation || 1).padStart(2, '0')}`;
    const version = '01';
    const trainers = plan.formateur ? `${(plan.formateur.prenom || '').trim()} ${(plan.formateur.nom || '').trim()}`.trim() || '—' : '—';
    
    // Format dates
    const dateDebut = plan.dateDebut ? new Date(plan.dateDebut).toLocaleDateString('fr-FR') : '—';
    const dateFin = plan.dateFin ? new Date(plan.dateFin).toLocaleDateString('fr-FR') : '—';
    
    // Format status
    const statusLabels: Record<string, string> = {
      'EN_COURS': 'En cours',
      'TERMINEE': 'Terminée',
      'PLANIFIEE': 'Planifiée',
      'ANNULEE': 'Annulée'
    };
    const statusFormation = (plan as any).statusFormation || (plan as any).status;
    const statusDisplay = statusFormation ? statusLabels[statusFormation] || statusFormation : '—';
    
    // Description from plan
    const planDescription = plan.description || '';

    // Calculate total duration and total days
    let totalDuration = 0;
    let totalDays = 0;
    const uniqueDays = new Set<number>();
    
    (formation.programmesDetailes || []).forEach(prog => {
      (prog.jours || []).forEach(jour => {
        uniqueDays.add(jour.numeroJour);
        (jour.contenus || []).forEach(c => {
          totalDuration += (c.dureeTheorique || 0) + (c.dureePratique || 0);
        });
      });
    });
    
    totalDays = uniqueDays.size;

    // Build programme tables per programme
    const programmeSections = this.generateProgrammeDetailsTable(formation, includeLinks);

    // Content links are now shown directly in the "Méthodes et Moyens Pédagogiques" column - no separate section needed
    const contentLinksSection = '';

    return `<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Fiche Programme - ${formation.theme}</title>
  <style>
    @page {
      size: A4;
      margin: 15mm 10mm;
    }
    
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }
    
    body {
      font-family: 'Calibri', 'Arial', sans-serif;
      font-size: 11pt;
      line-height: 1.4;
      color: #000;
      background: #fff;
    }
    
    .document {
      max-width: 210mm;
      margin: 0 auto;
      background: #fff;
      padding: 10mm;
    }
    
    /* Header Styles */
    .header {
      border: 2px solid #000;
      margin-bottom: 5mm;
    }
    
    .header-top {
      display: flex;
      border-bottom: 2px solid #000;
    }
    
    .header-left {
      flex: 1;
      padding: 8px 12px;
      border-right: 2px solid #000;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }
    
    .header-left h1 {
      font-size: 14pt;
      font-weight: bold;
      margin-bottom: 4px;
    }
    
    .header-left p {
      font-size: 10pt;
      margin: 2px 0;
    }
    
    .header-right {
      width: 35%;
      display: flex;
      flex-direction: column;
    }
    
    .header-cell {
      padding: 6px 10px;
      border-bottom: 1px solid #000;
      font-size: 10pt;
    }
    
    .header-cell:last-child {
      border-bottom: none;
    }
    
    .header-cell strong {
      font-weight: bold;
    }
    
    .header-bottom {
      display: flex;
    }
    
    .header-bottom-cell {
      flex: 1;
      padding: 6px 10px;
      border-right: 1px solid #000;
      font-size: 10pt;
      text-align: center;
    }
    
    .header-bottom-cell:last-child {
      border-right: none;
    }
    
    /* Info Section */
    .info-section {
      display: flex;
      gap: 3mm;
      margin-bottom: 5mm;
    }
    
    .info-box {
      flex: 1;
      border: 2px solid #000;
      padding: 8px 12px;
    }
    
    .info-box h2 {
      font-size: 11pt;
      font-weight: bold;
      margin-bottom: 6px;
      text-decoration: underline;
    }
    
    .info-box p {
      font-size: 10pt;
      margin: 3px 0;
    }
    
    .info-box ul {
      margin-left: 18px;
      font-size: 10pt;
    }
    
    .info-box li {
      margin: 2px 0;
    }
    
    /* Programme Section */
    .programme-section {
      margin-bottom: 5mm;
    }
    
    .objectif-section {
      page-break-inside: avoid;
      margin-bottom: 6mm;
    }
    
    .objectif-header {
      background: #d9d9d9;
      border: 2px solid #000;
      padding: 8px 12px;
      font-weight: bold;
      font-size: 11pt;
      margin-bottom: 0;
      page-break-after: avoid;
    }
    
    .program-table {
      width: 100%;
      border-collapse: separate !important;
      border-spacing: 0 !important;
      border: 2px solid #000;
      border-top: none;
      margin-bottom: 5mm;
    }
    
    .program-table th {
      background: #f0f0f0;
      border: 1px solid #000 !important;
      border-collapse: separate !important;
      padding: 8px 10px;
      font-size: 10pt;
      font-weight: bold;
      text-align: center;
      vertical-align: middle;
      page-break-after: avoid;
    }
    
    .program-table td {
      border: 1px solid #000 !important;
      border-collapse: separate !important;
      padding: 8px 10px;
      font-size: 10pt;
      vertical-align: top;
      page-break-inside: avoid;
    }
    
    .program-table tbody tr {
      page-break-inside: avoid;
    }
    
    .program-table .day-cell {
      text-align: center;
      font-weight: bold;
      width: 45px;
      min-width: 45px;
    }
    
    .program-table .objectif-cell {
      width: 70px;
      min-width: 70px;
    }
    
    .program-table .content-cell {
      width: 150px;
      min-width: 150px;
      word-wrap: break-word;
      padding: 8px 10px;
    }
    
    .program-table .content-cell ul {
      margin: 0;
      padding-left: 20px;
      list-style-type: disc;
    }
    
    .program-table .content-cell li {
      margin: 6px 0;
      line-height: 1.6;
      word-break: break-word;
      page-break-inside: avoid;
    }
    
    .program-table .content-cell a {
      color: #1976d2;
      text-decoration: underline;
      word-break: break-word;
    }
    
    .program-table .method-cell {
      width: 22%;
    }
    
    .program-table .duration-cell {
      text-align: center;
      width: 70px;
      font-weight: bold;
    }
    
    .program-table .content-link {
      color: #0066cc;
      text-decoration: none;
      font-size: 9pt;
      display: inline-block;
      margin-left: 8px;
      padding: 2px 6px;
      background: #e3f2fd;
      border-radius: 3px;
      border: 1px solid #0066cc;
    }
    
    .program-table .content-link:hover {
      background: #bbdefb;
    }
    
    /* Content Links Section */
    .content-links-section {
      border: 2px solid #000;
      padding: 8px 12px;
      margin-bottom: 5mm;
    }
    
    .content-links-section h2 {
      font-size: 11pt;
      font-weight: bold;
      margin-bottom: 8px;
      text-decoration: underline;
    }
    
    .content-links-section ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }
    
    .content-links-section li {
      margin: 4px 0;
      padding: 6px;
      background: #f9f9f9;
      border-left: 3px solid #0066cc;
    }
    
    .content-links-section a {
      color: #0066cc;
      text-decoration: none;
      font-size: 10pt;
    }
    
    .content-links-section a:hover {
      text-decoration: underline;
    }
    
    .content-type-badge {
      display: inline-block;
      padding: 2px 6px;
      background: #4caf50;
      color: white;
      border-radius: 3px;
      font-size: 8pt;
      margin-left: 6px;
    }
    
    /* Footer */
    .footer {
      border: 2px solid #000;
      padding: 8px 12px;
      margin-top: 5mm;
    }
    
    .footer p {
      font-size: 10pt;
      margin: 3px 0;
    }
    
    .footer strong {
      font-weight: bold;
    }
    
    @media print {
      body {
        background: #fff;
      }
      
      .document {
        padding: 0;
      }
      
      .programme-section {
        page-break-inside: avoid;
      }
      
      .objectif-header {
        page-break-after: avoid;
      }
      
      /* Force borders to be visible in print */
      .program-table {
        border-collapse: separate !important;
        border-spacing: 0 !important;
      }
      
      .program-table,
      .program-table th,
      .program-table td {
        border: 1px solid #000 !important;
        border-color: #000 !important;
        -webkit-print-color-adjust: exact !important;
        print-color-adjust: exact !important;
        color-adjust: exact !important;
      }
      
      .program-table th {
        background-color: #f0f0f0 !important;
        -webkit-print-color-adjust: exact !important;
        print-color-adjust: exact !important;
      }
    }
  </style>
</head>
<body>
  <div class="document">
    <!-- Header -->
    <div class="header">
      <div class="header-top">
        <div class="header-left">
          <h1>FICHE PROGRAMME DE FORMATION</h1>
          <p><strong>${formation.theme}</strong></p>
        </div>
        <div class="header-right">
          <div class="header-cell">
            <strong>Référence:</strong> ${refNumber}
          </div>
          <div class="header-cell">
            <strong>Version:</strong> ${version}
          </div>
          <div class="header-cell">
            <strong>Date d'application:</strong> ${today}
          </div>
          <div class="header-cell">
            <strong>Statut:</strong> ${statusDisplay}
          </div>
        </div>
      </div>
      <div class="header-bottom">
        <div class="header-bottom-cell">
          <strong>Formateur:</strong> ${trainers}
        </div>
        <div class="header-bottom-cell">
          <strong>Date début:</strong> ${dateDebut}
        </div>
        <div class="header-bottom-cell">
          <strong>Date fin:</strong> ${dateFin}
        </div>
      </div>
    </div>

    ${planDescription ? `
    <!-- Plan Description -->
    <div class="info-section">
      <div class="info-box" style="flex: 1;">
        <h2>Description du Plan</h2>
        <p>${planDescription}</p>
      </div>
    </div>
    ` : ''}

    <!-- Info Boxes -->
    <div class="info-section">
      <div class="info-box">
        <h2>Population cible</h2>
        <p>${formation.populationCible || 'Non spécifié'}</p>
      </div>
      <div class="info-box">
        <h2>Durée</h2>
        <p>${totalDuration} heures</p>
        <p style="margin-top: 4px; font-size: 10pt; color: #666;">(${totalDays} jour${totalDays > 1 ? 's' : ''})</p>
      </div>
      <div class="info-box">
        <h2>Lieu</h2>
        <p>${formation.typeFormation || 'Non spécifié'}</p>
      </div>
    </div>

    <div class="info-section">
      <div class="info-box" style="flex: 2;">
        <h2>Objectif général</h2>
        ${(() => {
          const globaux = ((formation as any).objectifsGlobaux || [])
            .map((o: any) => o.libelle || o.titre || o.label)
            .filter((t: string | undefined) => !!t);
          const fallback = this.cleanHtmlContent(formation.objectifGlobal || '');
          if (globaux.length) {
            return `<ul>${globaux.map((t: string) => `<li>${t}</li>`).join('')}</ul>`;
          }
          if (fallback) {
            return `<p>${fallback}</p>`;
          }
          return '<p>Non spécifié</p>';
        })()}
      </div>
    </div>

    <div class="info-section">
      <div class="info-box" style="flex: 2;">
        <h2>Objectifs spécifiques</h2>
        ${(() => {
          const specs = ((formation as any).objectifsSpecifiques || [])
            .map((o: any) => o.titre || o.libelle || o.label)
            .filter((t: string | undefined) => !!t);
          const fallback = (formation.programmesDetailes || []).map(p => p.titre).filter(t => !!t);
          const items = specs.length ? specs : fallback;
          return items.length
            ? `<ul>${items.map((t: string) => `<li>${t}</li>`).join('')}</ul>`
            : '<p>Non spécifié</p>';
        })()}
      </div>
    </div>

    <!-- Programme Tables -->
    <div class="programme-section">
      ${programmeSections}
    </div>

    <!-- Content Links Section (only if includeLinks is true) -->
    ${contentLinksSection}

    <!-- Footer -->
    <div class="footer">
      <p><strong>Méthodes d'évaluation:</strong> Évaluation continue, QCM, exercices pratiques, mise en situation</p>
      <p><strong>Formateur(s):</strong> ${trainers}</p>
      <p><strong>Prérequis:</strong> ${formation.niveau || 'Aucun prérequis spécifique'}</p>
    </div>
  </div>
</body>
</html>`;
  }

  private generateProgrammeDetailsTable(formation: Formation, includeLinks: boolean = false): string {
    
    // Get base URL for file links from environment
    const baseUrl = environment.production ? 'https://api.platform.sms2i.com.tn/api' : 'http://localhost:8080/api';
    
    // Build a map of jour number -> files for quick lookup
    const jourFilesMap: Map<number, Array<{titre: string, files: any[]}>> = new Map();
    if (formation.contenusWithJours && formation.contenusWithJours.length > 0) {
      formation.contenusWithJours.forEach(contenu => {
        if (contenu.numeroJour && contenu.files && contenu.files.length > 0) {
          if (!jourFilesMap.has(contenu.numeroJour)) {
            jourFilesMap.set(contenu.numeroJour, []);
          }
          jourFilesMap.get(contenu.numeroJour)!.push({
            titre: contenu.titre,
            files: contenu.files
          });
        }
      });
    }
    
    if (!formation.programmesDetailes || formation.programmesDetailes.length === 0) {
      return '';
    }

    // Step 1: Collect all jours from all objectifs and group by jour number
    interface JourData {
      numeroJour: number;
      objectifTitre: string;
      objectifIndex: number;
      contenus: any[];
    }
    
    const allJoursMap: Map<number, JourData[]> = new Map();
    
    formation.programmesDetailes.forEach((programme, progIndex) => {
      if (programme.jours && programme.jours.length > 0) {
        programme.jours.forEach(jour => {
          const jourNum = jour.numeroJour;
          if (!allJoursMap.has(jourNum)) {
            allJoursMap.set(jourNum, []);
          }
          allJoursMap.get(jourNum)!.push({
            numeroJour: jourNum,
            objectifTitre: programme.titre,
            objectifIndex: progIndex + 1,
            contenus: jour.contenus || []
          });
        });
      }
    });
    
    // Step 2: Sort jours by number
    const sortedJourNumbers = Array.from(allJoursMap.keys()).sort((a, b) => a - b);
    
    // Step 3: Generate a single table ordered by jours
    let html = `
      <div class="objectif-section">
        <div class="objectif-header">Programme Détaillé par Jour</div>
        
        <table class="program-table">
          <thead>
            <tr>
              <th rowspan="2">Jours</th>
              <th rowspan="2">Objectif Spécifique</th>
              <th rowspan="2">Contenus/<br/>Concepts Clés à<br/>aborder</th>
              <th rowspan="2">Méthodes et<br/>Moyens<br/>Pédagogiques</th>
              <th colspan="2">Durée (Heures)</th>
            </tr>
            <tr>
              <th>Théorique</th>
              <th>Pratique</th>
            </tr>
          </thead>
          <tbody>
    `;

    sortedJourNumbers.forEach(jourNum => {
      const joursData = allJoursMap.get(jourNum)!;
      
      // For each jour, we may have multiple objectifs
      joursData.forEach((jourData, jourDataIdx) => {
        // Calculate totals for this objectif on this jour from all levels
        let totalTheorique = 0;
        let totalPratique = 0;
        
        const allContents: Array<{text: string, contenu: any}> = [];
        const allMethods: string[] = [];
        
        console.log(`📄 PDF Jour ${jourNum} jourData.contenus:`, jourData.contenus);
        jourData.contenus.forEach((contenu: any) => {
          console.log(`📄 PDF Contenu:`, { levels: contenu.levels, dureeTheorique: contenu.dureeTheorique, dureePratique: contenu.dureePratique });
          // First try to get hours from levels (support de cours)
          if (contenu.levels && Array.isArray(contenu.levels) && contenu.levels.length > 0) {
            contenu.levels.forEach((level: any) => {
              totalTheorique += level.dureeTheorique || 0;
              totalPratique += level.dureePratique || 0;
            });
          } else {
            // Fallback to contenu level hours
            totalTheorique += contenu.dureeTheorique || 0;
            totalPratique += contenu.dureePratique || 0;
          }
          console.log(`📄 PDF After adding contenu hours - totalTheorique=${totalTheorique}, totalPratique=${totalPratique}`);
          
          // Try to get contenusCles first
          if (contenu.contenusCles && contenu.contenusCles.length > 0) {
            contenu.contenusCles.forEach((item: string) => {
              const cleanItem = this.cleanHtmlContent(item);
              if (cleanItem && cleanItem.length > 0) {
                allContents.push({text: cleanItem, contenu: contenu});
              }
            });
          } else if (contenu.titre) {
            // Fallback: if no contenusCles, use titre
            const cleanTitre = this.cleanHtmlContent(contenu.titre);
            if (cleanTitre && cleanTitre.length > 0) {
              allContents.push({text: cleanTitre, contenu: contenu});
            }
          }
          
          if (contenu.methodesPedagogiques) {
            const cleanMethod = this.cleanHtmlContent(contenu.methodesPedagogiques);
            if (cleanMethod) {
              allMethods.push(cleanMethod);
            }
          }
        });
        
        html += '<tr>';
        
        // Jour cell - only show on first row for this jour number
        if (jourDataIdx === 0) {
          const rowspan = joursData.length;
          html += `<td class="day-cell" ${rowspan > 1 ? `rowspan="${rowspan}"` : ''}>Jour ${jourNum}</td>`;
        }
        
        // Objectif cell
        html += `<td class="objectif-cell" style="font-weight: 500; font-size: 9pt;">${jourData.objectifTitre}</td>`;
        
        // Content cell
        html += '<td class="content-cell">';
        if (allContents.length > 0) {
          html += '<ul>';
          allContents.forEach(({text, contenu}) => {
            const pdfFile = this.findFirstPdfInContenu(contenu as any);
            
            if (pdfFile && includeLinks) {
              const fileName = (pdfFile.fileName || pdfFile.filePath || '').toLowerCase();
              
              // Document extensions that Google Viewer can handle
              const documentExtensions = ['.pdf', '.ppt', '.pptx', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
              const mediaExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg', '.mp4', '.webm', '.ogg', '.mov', '.avi', '.mkv'];
              
              const isDocument = documentExtensions.some(ext => fileName.endsWith(ext));
              const isMedia = mediaExtensions.some(ext => fileName.endsWith(ext));
              
              let viewUrl: string;
              if (isDocument) {
                const fileUrl = `${baseUrl}/contenus-detailles/files/${pdfFile.filePath}`;
                viewUrl = `https://docs.google.com/viewer?url=${encodeURIComponent(fileUrl)}&embedded=true`;
              } else if (isMedia) {
                viewUrl = `${baseUrl}/contenus-detailles/view/${pdfFile.filePath}`;
              } else {
                viewUrl = `${baseUrl}/contenus-detailles/files/${pdfFile.filePath}`;
              }
              
              html += `<li><a href="${viewUrl}" target="_blank" style="color: #1976d2; text-decoration: underline; cursor: pointer;">${text} 📄</a></li>`;
            } else {
              const escapedItem = text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
              html += `<li>${escapedItem}</li>`;
            }
          });
          html += '</ul>';
        }
        html += '</td>';
        
        // Method cell - Show file download links
        html += '<td class="method-cell">';
        
        if (includeLinks) {
          const allFiles: Array<{fileName: string, filePath: string, titre: string, dureeTheorique?: number, dureePratique?: number, levelName?: string}> = [];
          
          // First try: from formation.contenusWithJours (backend endpoint)
          if (jourFilesMap.has(jourNum)) {
            const jourContenus = jourFilesMap.get(jourNum)!;
            jourContenus.forEach(contenu => {
              // Check if contenu has levels with hours
              if ((contenu as any).levels && Array.isArray((contenu as any).levels)) {
                (contenu as any).levels.forEach((level: any) => {
                  const levelNames = ['Débutant', 'Intermédiaire', 'Avancé'];
                  const levelName = levelNames[level.levelNumber - 1] || `Niveau ${level.levelNumber}`;
                  if (level.files && Array.isArray(level.files)) {
                    level.files.forEach((file: any) => {
                      if (file.filePath) {
                        allFiles.push({
                          fileName: file.fileName || contenu.titre || 'Document',
                          filePath: file.filePath,
                          titre: contenu.titre,
                          dureeTheorique: level.dureeTheorique || 0,
                          dureePratique: level.dureePratique || 0,
                          levelName: levelName
                        });
                      }
                    });
                  }
                });
              }
              // Fallback to direct files without level hours
              contenu.files.forEach(file => {
                // Don't add if already added from levels
                const alreadyAdded = allFiles.some(f => f.filePath === file.filePath);
                if (file.filePath && !alreadyAdded) {
                  allFiles.push({
                    fileName: file.fileName || contenu.titre || 'Document',
                    filePath: file.filePath,
                    titre: contenu.titre
                  });
                }
              });
            });
          }
          
          // Second try: from jourData.contenus directly (local data)
          if (allFiles.length === 0 && jourData.contenus) {
            jourData.contenus.forEach((contenu: any) => {
              if (contenu.levels && Array.isArray(contenu.levels)) {
                contenu.levels.forEach((level: any) => {
                  const levelNames = ['Débutant', 'Intermédiaire', 'Avancé'];
                  const levelName = levelNames[level.levelNumber - 1] || `Niveau ${level.levelNumber}`;
                  if (level.files && Array.isArray(level.files)) {
                    level.files.forEach((file: any) => {
                      if (file.filePath) {
                        allFiles.push({
                          fileName: file.fileName || contenu.titre || 'Document',
                          filePath: file.filePath,
                          titre: contenu.titre,
                          dureeTheorique: level.dureeTheorique || 0,
                          dureePratique: level.dureePratique || 0,
                          levelName: levelName
                        });
                      }
                    });
                  }
                });
              }
              if (contenu.files && Array.isArray(contenu.files)) {
                contenu.files.forEach((file: any) => {
                  // Don't add if already added from levels
                  const alreadyAdded = allFiles.some(f => f.filePath === file.filePath);
                  if (file.filePath && !alreadyAdded) {
                    allFiles.push({
                      fileName: file.fileName || contenu.titre || 'Document',
                      filePath: file.filePath,
                      titre: contenu.titre
                    });
                  }
                });
              }
            });
          }
          
          // Display the files (only on first row for this jour to avoid duplication)
          if (allFiles.length > 0 && jourDataIdx === 0) {
            html += '<div style="padding: 4px 0;">';
            html += '<strong style="font-size: 9pt; color: #333;">📎 Supports:</strong>';
            html += '<ul style="margin: 4px 0; padding-left: 15px; font-size: 9pt;">';
            allFiles.forEach(file => {
              const fileName = file.fileName.toLowerCase();
              
              // Document extensions that Google Viewer can handle
              const documentExtensions = ['.pdf', '.ppt', '.pptx', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
              const mediaExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg', '.mp4', '.webm', '.ogg', '.mov', '.avi', '.mkv'];
              
              const isDocument = documentExtensions.some(ext => fileName.endsWith(ext));
              const isMedia = mediaExtensions.some(ext => fileName.endsWith(ext));
              
              let viewUrl: string;
              if (isDocument) {
                // Use Google Docs Viewer for documents (no download, view only)
                const fileUrl = `${baseUrl}/contenus-detailles/files/${file.filePath}`;
                viewUrl = `https://docs.google.com/viewer?url=${encodeURIComponent(fileUrl)}&embedded=true`;
              } else if (isMedia) {
                // Use custom view endpoint for videos and images (no download)
                viewUrl = `${baseUrl}/contenus-detailles/view/${file.filePath}`;
              } else {
                // Other files - open directly
                viewUrl = `${baseUrl}/contenus-detailles/files/${file.filePath}`;
              }
              
              // Build hours display string
              let hoursDisplay = '';
              if (file.dureeTheorique || file.dureePratique) {
                const parts = [];
                if (file.dureeTheorique) {
                  parts.push(`Théo: ${file.dureeTheorique}h`);
                }
                if (file.dureePratique) {
                  parts.push(`Prat: ${file.dureePratique}h`);
                }
                hoursDisplay = ` <span style="color: #666; font-size: 8pt;">(${parts.join(' / ')})</span>`;
              }
              
              // Add level name if available
              let levelDisplay = '';
              if (file.levelName) {
                levelDisplay = ` <span style="background: #e3f2fd; color: #1565c0; font-size: 7pt; padding: 1px 4px; border-radius: 3px;">${file.levelName}</span>`;
              }
              
              html += `<li><a href="${viewUrl}" target="_blank" style="color: #1976d2; text-decoration: underline;">${file.fileName}</a>${levelDisplay}${hoursDisplay}</li>`;
            });
            html += '</ul></div>';
          }
        } else {
          // For simple PDF (without links), show the method text
          if (allMethods.length > 0) {
            const uniqueMethods = [...new Set(allMethods)];
            html += uniqueMethods.join(' + ');
          }
        }
        html += '</td>';
        
        // Duration cells
        html += `<td class="duration-cell">${totalTheorique ? totalTheorique + 'h' : ''}</td>`;
        html += `<td class="duration-cell">${totalPratique ? totalPratique + 'h' : ''}</td>`;
        
        html += '</tr>';
      });
    });

    html += `
          </tbody>
        </table>
      </div>
    `;

    return html;
  }

  private generateContentLinksSection(formation: Formation): string {
    console.log('📚 Generating content links section...');
    console.log('Formation contenusFormation:', formation.contenusFormation);
    
    if (!formation.contenusFormation || formation.contenusFormation.length === 0) {
      console.log('⚠️ No contenusFormation found - skipping content links section');
      return '';
    }

    // Get base URL for Angular app routing (not API)
    const appBaseUrl = environment.production ? 'https://platform.sms2i.com.tn' : 'http://localhost:4200';
    
    let html = `
      <div class="content-links-section" style="margin-top: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px; border: 1px solid #dee2e6;">
        <h2>📚 Contenus de Formation Disponibles</h2>
        <p style="font-size: 10pt; color: #666; margin-bottom: 8px;">Cliquez sur les liens pour accéder aux contenus</p>
        <ul style="list-style-type: disc; padding-left: 20px;">
    `;

    formation.contenusFormation.forEach(content => {
      const typeColors: any = {
        'PDF': '#f44336',
        'VIDEO': '#2196f3',
        'DOCUMENT': '#4caf50',
        'PRESENTATION': '#ff9800'
      };
      const bgColor = typeColors[content.type] || '#4caf50';
      
      // Build proper URL - use Angular app route for editing the content
      // This navigates to /formation/contenu/:id to edit the contenu détaillé
      const contentUrl = content.id ? `${appBaseUrl}/formation/contenu/${content.id}` : '#';
      
      html += `
        <li style="margin-bottom: 8px;">
          • <a href="${contentUrl}" target="_blank" style="color: #1976d2; text-decoration: underline;">
            ${content.title}
          </a>
          <span class="content-type-badge" style="background: ${bgColor}; color: white; padding: 2px 8px; border-radius: 4px; font-size: 8pt; margin-left: 8px;">${content.type}</span>
          ${content.description ? `<br/><small style="color: #666; margin-left: 12px;">${content.description}</small>` : ''}
          ${content.fileName ? `<br/><small style="color: #999; margin-left: 12px;">📎 ${content.fileName}</small>` : ''}
        </li>
      `;
    });

    html += `
        </ul>
      </div>
    `;

    console.log('✅ Content links section generated with', formation.contenusFormation.length, 'items');
    return html;
  }

  private findContentFileForContenu(formation: Formation, contenuDetailleId: number): any {
    // This is a simple matching logic - you might need to adjust based on your data structure
    // For now, we'll return null as the mapping between contenuDetaille and contenusFormation
    // needs to be defined in your backend
    if (!formation.contenusFormation || formation.contenusFormation.length === 0) {
      return null;
    }
    
    // You could add a reference field in ContenuDetaille to link to ContenuFormation
    // For now, return the first content file as a placeholder
    return null;
  }

  private findFirstPdfInContenu(contenu: any): any {
    // Check if contenu has levels with files
    if (!contenu.levels || !Array.isArray(contenu.levels)) {
      // Fallback: Check if the 'files' property exists directly (flat structure)
      if (contenu.files && Array.isArray(contenu.files)) {
         const pdfFile = contenu.files.find((file: any) => 
          file.fileType && (file.fileType.includes('pdf') || file.fileName?.endsWith('.pdf'))
        );
        if (pdfFile) return pdfFile;
      }
      return null;
    }

    // Search through all levels for the first PDF file
    for (const level of contenu.levels) {
      if (level.files && Array.isArray(level.files)) {
        const pdfFile = level.files.find((file: any) => 
          file.fileType && (file.fileType.includes('pdf') || file.fileName?.endsWith('.pdf'))
        );
        if (pdfFile) {
          return pdfFile;
        }
      }
    }

    return null;
  }
}
