import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ManifestationService } from '../../../services/manifestation.service';
import { WorkflowService } from '../../../services/workflow.service';
import { AssignmentService } from '../../../services/assignment.service';
import { LegalImpedimentService } from '../../../services/legal-impediment.service';
import { DecisionService } from '../../../services/decision.service';
import { EvaluationService } from '../../../services/evaluation.service';
import { AuditService } from '../../../services/audit.service';
import { AnalystService } from '../../../services/analyst.service';
import { AuthService } from '../../../services/auth.service';
import {
  MAX_APPEALS,
  ManifestationResponse,
  ManifestationStatus,
  STATUS_LABELS,
  TYPE_LABELS,
} from '../../../models/manifestation.model';
import { AnalystResponse } from '../../../models/analyst.model';
import { ResponsibleAssignmentResponse } from '../../../models/assignment.model';
import { ImpedimentReason, LegalImpedimentResponse, REASON_LABELS } from '../../../models/legal-impediment.model';
import { DECISION_TYPE_LABELS, DecisionRecordResponse, DecisionType } from '../../../models/decision.model';
import { ServiceEvaluationResponse } from '../../../models/evaluation.model';
import { AuditEntryResponse } from '../../../models/audit.model';

type Tab = 'overview' | 'designation' | 'impediments' | 'decisions' | 'evaluation' | 'audit';

const NEXT_STATUS: Partial<Record<ManifestationStatus, ManifestationStatus>> = {
  REGISTERED: 'IN_REVIEW',
  IN_REVIEW: 'RESOLVED',
  RESOLVED: 'CLOSED',
};

@Component({
  selector: 'app-manifestation-detail',
  imports: [RouterLink, DatePipe, ReactiveFormsModule, FormsModule],
  templateUrl: './manifestation-detail.html',
  styleUrl: './manifestation-detail.css',
})
export class ManifestationDetailPage implements OnInit {
  private readonly manifestationService = inject(ManifestationService);
  private readonly workflowService = inject(WorkflowService);
  private readonly assignmentService = inject(AssignmentService);
  private readonly legalImpedimentService = inject(LegalImpedimentService);
  private readonly decisionService = inject(DecisionService);
  private readonly evaluationService = inject(EvaluationService);
  private readonly auditService = inject(AuditService);
  private readonly analystService = inject(AnalystService);
  protected readonly auth = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly STATUS_LABELS = STATUS_LABELS;
  protected readonly TYPE_LABELS = TYPE_LABELS;
  protected readonly REASON_LABELS = REASON_LABELS;
  protected readonly DECISION_TYPE_LABELS = DECISION_TYPE_LABELS;
  protected readonly MAX_APPEALS = MAX_APPEALS;

  protected manifestationId = 0;
  protected manifestation = signal<ManifestationResponse | null>(null);
  protected loading = signal(true);
  protected error = signal('');
  protected deleting = signal(false);

  protected activeTab = signal<Tab>('overview');
  protected analysts = signal<AnalystResponse[]>([]);

  // Workflow actions
  protected advancing = signal(false);
  protected workflowError = signal('');

  // Designation
  protected assignment = signal<ResponsibleAssignmentResponse | null>(null);
  protected loadingAssignment = signal(true);
  protected assigning = signal(false);
  protected assignmentError = signal('');
  protected selectedAnalystId = signal<number | null>(null);

  // Legal impediments
  protected impediments = signal<LegalImpedimentResponse[]>([]);
  protected loadingImpediments = signal(true);
  protected impedimentForm = this.fb.group({
    analystId: ['', Validators.required],
    reason: ['' as ImpedimentReason | '', Validators.required],
  });
  protected savingImpediment = signal(false);
  protected impedimentError = signal('');

  // Decisions
  protected decisions = signal<DecisionRecordResponse[]>([]);
  protected loadingDecisions = signal(true);
  protected decisionForm = this.fb.group({
    type: ['' as DecisionType | '', Validators.required],
    content: ['', [Validators.required, Validators.maxLength(4000)]],
  });
  protected savingDecision = signal(false);
  protected decisionError = signal('');

  // Evaluation (somente leitura — quem avalia é o cidadão, pelo portal público)
  protected evaluation = signal<ServiceEvaluationResponse | null>(null);
  protected loadingEvaluation = signal(true);
  protected evaluationError = signal('');

  // Audit
  protected auditEntries = signal<AuditEntryResponse[]>([]);
  protected loadingAudit = signal(true);
  protected auditForm = this.fb.group({
    action: ['', [Validators.required, Validators.maxLength(255)]],
    description: ['', Validators.maxLength(1000)],
  });
  protected savingAudit = signal(false);
  protected auditError = signal('');

  protected readonly ratings = [1, 2, 3, 4, 5];

  ngOnInit(): void {
    this.manifestationId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadManifestation();
    this.analystService.list().subscribe({ next: list => this.analysts.set(list) });
  }

  private loadManifestation(): void {
    this.manifestationService.findById(this.manifestationId).subscribe({
      next: m => {
        this.manifestation.set(m);
        this.loading.set(false);
        this.loadAssignment();
        this.loadImpediments();
        this.loadDecisions();
        this.loadEvaluation();
        this.loadAudit();
      },
      error: () => {
        this.error.set('Manifestação não encontrada.');
        this.loading.set(false);
      },
    });
  }

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
  }

  analystName(id: number | null | undefined): string {
    if (!id) return '—';
    return this.analysts().find(a => a.id === id)?.name ?? `#${id}`;
  }

  nextStatus(): ManifestationStatus | null {
    const status = this.manifestation()?.status;
    return status ? (NEXT_STATUS[status] ?? null) : null;
  }

  advance(): void {
    const next = this.nextStatus();
    if (!next) return;

    this.advancing.set(true);
    this.workflowError.set('');
    this.workflowService.advance(this.manifestationId).subscribe({
      next: updated => {
        this.manifestation.set(updated);
        this.advancing.set(false);
      },
      error: () => {
        this.workflowError.set('Não foi possível avançar o status.');
        this.advancing.set(false);
      },
    });
  }

  delete(): void {
    if (!confirm('Confirma a exclusão desta manifestação?')) return;

    this.deleting.set(true);
    this.manifestationService.delete(this.manifestation()!.id).subscribe({
      next: () => this.router.navigate(['/manifestacoes']),
      error: () => this.deleting.set(false),
    });
  }

  statusClass(status: string): string {
    return `badge-status-${status}`;
  }

  // ---- Designation ----

  private loadAssignment(): void {
    this.loadingAssignment.set(true);
    this.assignmentService.findByManifestationId(this.manifestationId).subscribe({
      next: a => {
        this.assignment.set(a);
        this.loadingAssignment.set(false);
      },
      error: () => {
        this.assignment.set(null);
        this.loadingAssignment.set(false);
      },
    });
  }

  autoAssign(): void {
    this.assigning.set(true);
    this.assignmentError.set('');
    this.assignmentService.autoAssign(this.manifestationId).subscribe({
      next: a => {
        this.assignment.set(a);
        this.assigning.set(false);
      },
      error: () => {
        this.assignmentError.set(
          'Não foi possível designar automaticamente (nenhum analista de especialidade ou região compatível).',
        );
        this.assigning.set(false);
      },
    });
  }

  manualAssign(): void {
    const analystId = this.selectedAnalystId();
    if (!analystId) return;

    this.assigning.set(true);
    this.assignmentError.set('');
    this.assignmentService
      .assign({
        manifestationId: this.manifestationId,
        responsibleId: analystId,
        assignedById: this.auth.currentUser()?.id,
      })
      .subscribe({
        next: a => {
          this.assignment.set(a);
          this.assigning.set(false);
          this.selectedAnalystId.set(null);
        },
        error: () => {
          this.assignmentError.set('Não foi possível designar o analista selecionado.');
          this.assigning.set(false);
        },
      });
  }

  unassign(): void {
    if (!confirm('Remover a designação atual?')) return;
    this.assigning.set(true);
    this.assignmentService.unassign(this.manifestationId).subscribe({
      next: () => {
        this.assignment.set(null);
        this.assigning.set(false);
      },
      error: () => {
        this.assignmentError.set('Não foi possível remover a designação.');
        this.assigning.set(false);
      },
    });
  }

  // ---- Legal impediments ----

  private loadImpediments(): void {
    this.loadingImpediments.set(true);
    this.legalImpedimentService.findByManifestationId(this.manifestationId).subscribe({
      next: list => {
        this.impediments.set(list);
        this.loadingImpediments.set(false);
      },
      error: () => {
        this.loadingImpediments.set(false);
      },
    });
  }

  registerImpediment(): void {
    if (this.impedimentForm.invalid) {
      this.impedimentForm.markAllAsTouched();
      return;
    }

    this.savingImpediment.set(true);
    this.impedimentError.set('');
    const { analystId, reason } = this.impedimentForm.getRawValue();

    this.legalImpedimentService
      .register({
        manifestationId: this.manifestationId,
        analystId: Number(analystId),
        reason: reason as ImpedimentReason,
      })
      .subscribe({
        next: () => {
          this.impedimentForm.reset();
          this.loadImpediments();
          this.savingImpediment.set(false);
        },
        error: () => {
          this.impedimentError.set('Não foi possível registrar o impedimento.');
          this.savingImpediment.set(false);
        },
      });
  }

  removeImpediment(id: number): void {
    if (!confirm('Remover este impedimento legal?')) return;
    this.legalImpedimentService.remove(id).subscribe({
      next: () => this.loadImpediments(),
      error: () => this.impedimentError.set('Não foi possível remover o impedimento.'),
    });
  }

  // ---- Decisions ----

  private loadDecisions(): void {
    this.loadingDecisions.set(true);
    this.decisionService.findAllByManifestationId(this.manifestationId).subscribe({
      next: list => {
        this.decisions.set(list);
        this.loadingDecisions.set(false);
      },
      error: () => {
        this.loadingDecisions.set(false);
      },
    });
  }

  createDecision(): void {
    if (this.decisionForm.invalid) {
      this.decisionForm.markAllAsTouched();
      return;
    }

    this.savingDecision.set(true);
    this.decisionError.set('');
    const { type, content } = this.decisionForm.getRawValue();

    this.decisionService
      .create({
        manifestationId: this.manifestationId,
        authorId: this.auth.currentUser()!.id,
        type: type as DecisionType,
        content: content!,
      })
      .subscribe({
        next: () => {
          this.decisionForm.reset();
          this.loadDecisions();
          this.savingDecision.set(false);
        },
        error: () => {
          this.decisionError.set('Não foi possível registrar a decisão/parecer.');
          this.savingDecision.set(false);
        },
      });
  }

  removeDecision(id: number): void {
    if (!confirm('Remover este registro?')) return;
    this.decisionService.delete(id).subscribe({
      next: () => this.loadDecisions(),
      error: () => this.decisionError.set('Não foi possível remover o registro.'),
    });
  }

  // ---- Evaluation ----

  private loadEvaluation(): void {
    this.loadingEvaluation.set(true);
    this.evaluationService.findByManifestationId(this.manifestationId).subscribe({
      next: ev => {
        this.evaluation.set(ev);
        this.loadingEvaluation.set(false);
      },
      error: () => {
        this.evaluation.set(null);
        this.loadingEvaluation.set(false);
      },
    });
  }

  // ---- Audit ----

  private loadAudit(): void {
    this.loadingAudit.set(true);
    this.auditService.findAllByManifestationId(this.manifestationId).subscribe({
      next: page => {
        this.auditEntries.set(page.content);
        this.loadingAudit.set(false);
      },
      error: () => {
        this.loadingAudit.set(false);
      },
    });
  }

  registerAuditEntry(): void {
    if (this.auditForm.invalid) {
      this.auditForm.markAllAsTouched();
      return;
    }

    this.savingAudit.set(true);
    this.auditError.set('');
    const { action, description } = this.auditForm.getRawValue();

    this.auditService
      .create({
        manifestationId: this.manifestationId,
        actorId: this.auth.currentUser()!.id,
        action: action!,
        description: description || undefined,
      })
      .subscribe({
        next: () => {
          this.auditForm.reset();
          this.loadAudit();
          this.savingAudit.set(false);
        },
        error: () => {
          this.auditError.set('Não foi possível registrar o evento de auditoria.');
          this.savingAudit.set(false);
        },
      });
  }
}
