import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ConflictCheckResponse,
  ResponsibleAssignmentRequest,
  ResponsibleAssignmentResponse,
} from '../models/assignment.model';

@Injectable({ providedIn: 'root' })
export class AssignmentService {
  private readonly http = inject(HttpClient);
  private readonly assignmentsBase = '/api/v1/assignments';
  private readonly designationsBase = '/api/v1/designations';

  assign(request: ResponsibleAssignmentRequest): Observable<ResponsibleAssignmentResponse> {
    return this.http.post<ResponsibleAssignmentResponse>(this.assignmentsBase, request);
  }

  findByManifestationId(manifestationId: number): Observable<ResponsibleAssignmentResponse> {
    return this.http.get<ResponsibleAssignmentResponse>(
      `${this.assignmentsBase}/manifestation/${manifestationId}`,
    );
  }

  unassign(manifestationId: number): Observable<void> {
    return this.http.delete<void>(`${this.assignmentsBase}/manifestation/${manifestationId}`);
  }

  autoAssign(manifestationId: number): Observable<ResponsibleAssignmentResponse> {
    return this.http.post<ResponsibleAssignmentResponse>(
      `${this.designationsBase}/${manifestationId}/auto`,
      {},
    );
  }

  checkConflict(manifestationId: number, analystId: number): Observable<ConflictCheckResponse> {
    return this.http.get<ConflictCheckResponse>(
      `${this.designationsBase}/${manifestationId}/conflict/${analystId}`,
    );
  }
}
