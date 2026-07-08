import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { BaseService } from '../base/base';
import { ResponsibleAssignmentRequest, ResponsibleAssignmentResponse } from '../../models/assignment.model';

@Injectable({ providedIn: 'root' })
export class AssignmentService extends BaseService<ResponsibleAssignmentResponse, ResponsibleAssignmentRequest> {

  constructor(http: HttpClient) {
    super(http, '/api/v1/assignments');
  }

  getByManifestation(manifestationId: number | string): Observable<ResponsibleAssignmentResponse> {
    return this.http
      .get<ResponsibleAssignmentResponse>(`${this.url}/manifestation/${manifestationId}`)
      .pipe(catchError(err => this.handleError(err)));
  }

  deleteByManifestation(manifestationId: number | string): Observable<void> {
    return this.http
      .delete<void>(`${this.url}/manifestation/${manifestationId}`)
      .pipe(catchError(err => this.handleError(err)));
  }
}
