import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { BaseService } from '../base/base';
import { DecisionRecordRequest, DecisionRecordResponse } from '../../models/decision.model';

@Injectable({ providedIn: 'root' })
export class DecisionService extends BaseService<DecisionRecordResponse, DecisionRecordRequest> {

  constructor(http: HttpClient) {
    super(http, '/api/v1/decisions');
  }

  listByManifestation(manifestationId: number | string): Observable<DecisionRecordResponse[]> {
    return this.http
      .get<DecisionRecordResponse[]>(`${this.url}/manifestation/${manifestationId}`)
      .pipe(catchError(err => this.handleError(err)));
  }
}
