import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize, switchMap } from 'rxjs/operators';
import { throwError } from 'rxjs';

import { ManifestationService } from '../../services/manifestation/manifestation.service';
import { DecisionService } from '../../services/decision/decision.service';
import { AssignmentService } from '../../services/assignment/assignment.service';
import { DesignationService } from '../../services/designation/designation.service';
import { AccusationService } from '../../services/accusation/accusation.service';
import { WorkflowService } from '../../services/workflow/workflow.service';
import { AttachmentService } from '../../services/attachment/attachment.service';
import { AuditService } from '../../services/audit/audit.service';
import { ResponseSuggestionService } from '../../services/response-suggestion/response-suggestion.service';
import { PdfService } from '../../services/pdf/pdf.service';
import { AuthService } from '../../services/auth/auth.service';
import { UserService } from '../../services/user/user';

import { ManifestationResponse, manifestationStatusLabel } from '../../models/manifestation.model';
import { DecisionRecordResponse, DecisionType } from '../../models/decision.model';
import { ResponsibleAssignmentResponse } from '../../models/assignment.model';
import { AccusationResponse } from '../../models/accusation.model';
import { AttachmentResponse } from '../../models/attachment.model';
import { AuditEntryResponse } from '../../models/audit.model';
import { UserResponse } from '../../models/usuario.model';

@Component({
  selector: 'app-parecer-preliminar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './parecer-preliminar.html',
  styleUrl: './parecer-preliminar.css'
})
export class ParecerPreliminar implements OnInit {
  manifestationId = 0;

  manifestation = signal<ManifestationResponse | null>(null);
  loading = signal(false);
  serverError = signal<string | null>(null);

  assignment = signal<ResponsibleAssignmentResponse | null>(null);
  assignResponsibleId: number | null = null;
  assigningManual = signal(false);
  assigningAuto = signal(false);

  accusations = signal<AccusationResponse[]>([]);
  accusedUserId: number | null = null;
  submittingAccusation = signal(false);

  decisions = signal<DecisionRecordResponse[]>([]);
  decisionType: DecisionType = 'DECISION';
  decisionContent = '';
  submittingDecision = signal(false);
  aiSuggestion = signal<string | null>(null);
  loadingSuggestion = signal(false);

  attachments = signal<AttachmentResponse[]>([]);
  selectedFile: File | null = null;
  uploadingAttachment = signal(false);

  auditEntries = signal<AuditEntryResponse[]>([]);

  users = signal<UserResponse[]>([]);

  advancingWorkflow = signal(false);
  exportingPdf = signal(false);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly manifestationService: ManifestationService,
    private readonly decisionService: DecisionService,
    private readonly assignmentService: AssignmentService,
    private readonly designationService: DesignationService,
    private readonly accusationService: AccusationService,
    private readonly workflowService: WorkflowService,
    private readonly attachmentService: AttachmentService,
    private readonly auditService: AuditService,
    private readonly responseSuggestionService: ResponseSuggestionService,
    private readonly pdfService: PdfService,
    private readonly authService: AuthService,
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.manifestationId = idParam ? Number(idParam) : NaN;
    if (!this.manifestationId || Number.isNaN(this.manifestationId)) return;
    this.loadAll();
  }

  private get currentUserId(): number {
    return this.authService.currentUser()?.id ?? 0;
  }

  statusLabel(status: string | undefined): string {
    return manifestationStatusLabel(status);
  }

  loadAll(): void {
    this.loading.set(true);
    this.manifestationService
      .getById(this.manifestationId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: m => this.manifestation.set(m),
        error: () => this.serverError.set('Manifestação não encontrada.')
      });

    this.refreshAssignment();
    this.refreshAccusations();
    this.refreshDecisions();
    this.refreshAttachments();
    this.refreshAudit();
    this.refreshUsers();
  }

  private refreshUsers(): void {
    // GET /v1/users exige ROLE_ADMIN; para outros papéis, cai no fallback por ID no template.
    this.userService.listAll().subscribe({
      next: list => this.users.set(list),
      error: () => this.users.set([])
    });
  }

  userName(id: number | null | undefined): string {
    if (id == null) return '-';
    const user = this.users().find(u => u.id === id);
    return user ? `${user.name} (${this.roleLabel(user.role)})` : `#${id}`;
  }

  roleLabel(role: string): string {
    switch (role) {
      case 'REMONSTRANT': return 'Reclamante';
      case 'LISTENER': return 'Ouvidor';
      case 'MANAGER': return 'Gestor';
      case 'GENERAL_LISTENER': return 'Ouvidor Geral';
      case 'ADMIN': return 'Administrador';
      default: return role;
    }
  }

  private refreshAssignment(): void {
    this.assignmentService.getByManifestation(this.manifestationId).subscribe({
      next: a => this.assignment.set(a),
      error: () => this.assignment.set(null)
    });
  }

  private refreshAccusations(): void {
    this.accusationService.list(this.manifestationId).subscribe({
      next: list => this.accusations.set(list),
      error: () => this.accusations.set([])
    });
  }

  private refreshDecisions(): void {
    this.decisionService.listByManifestation(this.manifestationId).subscribe({
      next: list => this.decisions.set(list),
      error: () => this.decisions.set([])
    });
  }

  private refreshAttachments(): void {
    this.attachmentService.list(this.manifestationId).subscribe({
      next: list => this.attachments.set(list),
      error: () => this.attachments.set([])
    });
  }

  private refreshAudit(): void {
    this.auditService.listByManifestation(this.manifestationId, 0, 50).subscribe({
      next: page => this.auditEntries.set(page.content),
      error: () => this.auditEntries.set([])
    });
  }

  private logAudit(action: string, description: string): void {
    this.auditService
      .create({ manifestationId: this.manifestationId, actorId: this.currentUserId, action, description })
      .subscribe({ next: () => this.refreshAudit(), error: () => {} });
  }

  assignManual(): void {
    if (!this.assignResponsibleId) return;
    const responsibleId = this.assignResponsibleId;
    this.assigningManual.set(true);
    this.serverError.set(null);

    this.designationService
      .checkConflict(this.manifestationId, responsibleId)
      .pipe(
        switchMap(result => {
          if (result.hasConflict) {
            return throwError(
              () =>
                'Este analista tem conflito de interesse com um acusado desta manifestação (é o próprio acusado, é do mesmo departamento, ou tem cargo inferior ao do acusado). Escolha outro analista ou use "Designar automaticamente".'
            );
          }
          return this.assignmentService.create({
            manifestationId: this.manifestationId,
            responsibleId,
            assignedById: this.currentUserId
          });
        }),
        finalize(() => this.assigningManual.set(false))
      )
      .subscribe({
        next: a => {
          this.assignment.set(a);
          this.logAudit('ASSIGNED_MANUAL', `Responsável ${a.responsibleId} atribuído manualmente.`);
        },
        error: err => this.serverError.set(typeof err === 'string' ? err : 'Não foi possível atribuir o responsável.')
      });
  }

  assignAuto(): void {
    this.assigningAuto.set(true);
    this.designationService
      .autoAssign(this.manifestationId)
      .pipe(finalize(() => this.assigningAuto.set(false)))
      .subscribe({
        next: a => {
          this.assignment.set(a);
          this.logAudit('ASSIGNED_AUTO', `Responsável ${a.responsibleId} designado automaticamente.`);
        },
        error: err => this.serverError.set(typeof err === 'string' ? err : 'Nenhum responsável elegível para designação automática.')
      });
  }

  addAccusation(): void {
    if (!this.accusedUserId) return;
    this.submittingAccusation.set(true);
    this.accusationService
      .create(this.manifestationId, this.accusedUserId)
      .pipe(finalize(() => this.submittingAccusation.set(false)))
      .subscribe({
        next: () => {
          this.accusedUserId = null;
          this.refreshAccusations();
          this.logAudit('ACCUSATION_REGISTERED', 'Acusado registrado para checagem de conflito de interesse.');
        },
        error: err => this.serverError.set(typeof err === 'string' ? err : 'Não foi possível registrar o acusado.')
      });
  }

  advanceWorkflow(): void {
    this.advancingWorkflow.set(true);
    this.workflowService
      .advance(this.manifestationId)
      .pipe(finalize(() => this.advancingWorkflow.set(false)))
      .subscribe({
        next: m => {
          this.manifestation.set(m);
          this.logAudit('WORKFLOW_ADVANCED', `Fase avançada para ${m.status}.`);
        },
        error: err => this.serverError.set(typeof err === 'string' ? err : 'Não foi possível avançar a fase do workflow.')
      });
  }

  requestSuggestion(): void {
    this.loadingSuggestion.set(true);
    this.responseSuggestionService
      .suggest(this.manifestationId)
      .pipe(finalize(() => this.loadingSuggestion.set(false)))
      .subscribe({
        next: res => this.aiSuggestion.set(res.suggestedResponse),
        error: () => this.serverError.set('Não foi possível obter sugestão da IA.')
      });
  }

  useSuggestion(): void {
    if (this.aiSuggestion()) {
      this.decisionContent = this.aiSuggestion()!;
    }
  }

  submitDecision(): void {
    if (!this.decisionContent.trim()) return;
    this.submittingDecision.set(true);
    this.decisionService
      .create({
        manifestationId: this.manifestationId,
        authorId: this.currentUserId,
        type: this.decisionType,
        content: this.decisionContent.trim()
      })
      .pipe(finalize(() => this.submittingDecision.set(false)))
      .subscribe({
        next: () => {
          this.decisionContent = '';
          this.aiSuggestion.set(null);
          this.refreshDecisions();
          this.logAudit('DECISION_ADDED', `Registro do tipo ${this.decisionType} adicionado.`);
        },
        error: err => this.serverError.set(typeof err === 'string' ? err : 'Não foi possível registrar a decisão.')
      });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  uploadAttachment(): void {
    if (!this.selectedFile) return;
    this.uploadingAttachment.set(true);
    this.attachmentService
      .upload(this.manifestationId, this.selectedFile)
      .pipe(finalize(() => this.uploadingAttachment.set(false)))
      .subscribe({
        next: () => {
          this.selectedFile = null;
          this.refreshAttachments();
        },
        error: () => this.serverError.set('Não foi possível anexar o arquivo.')
      });
  }

  downloadAttachment(attachment: AttachmentResponse): void {
    this.attachmentService.download(this.manifestationId, attachment.id).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = attachment.fileName;
      a.click();
      URL.revokeObjectURL(url);
    });
  }

  exportPdf(): void {
    this.exportingPdf.set(true);
    this.pdfService
      .export(this.manifestationId)
      .pipe(finalize(() => this.exportingPdf.set(false)))
      .subscribe(blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `manifestacao-${this.manifestationId}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      });
  }

  fileSizeLabel(size: number): string {
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
    return `${(size / (1024 * 1024)).toFixed(1)} MB`;
  }
}
