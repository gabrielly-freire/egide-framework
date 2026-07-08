import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { BaseService } from '../base/base';
import { ServiceEvaluationRequest, ServiceEvaluationResponse } from '../../models/evaluation.model';

@Injectable({ providedIn: 'root' })
export class EvaluationService extends BaseService<ServiceEvaluationResponse, ServiceEvaluationRequest> {

  constructor(http: HttpClient) {
    super(http, '/api/v1/evaluations');
  }

  getByManifestation(manifestationId: number | string): Observable<ServiceEvaluationResponse> {
    return this.http
      .get<ServiceEvaluationResponse>(`${this.url}/manifestation/${manifestationId}`)
      .pipe(catchError(err => this.handleError(err)));
  }
}
