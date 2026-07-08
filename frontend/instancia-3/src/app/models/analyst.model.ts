export interface AnalystRequest {
  name: string;
  specialty: string;
  region: string;
  email: string;
}

export interface AnalystResponse {
  id: number;
  name: string;
  email: string;
  specialty: string;
  region: string;
  createdAt: string;
  updatedAt: string;
}
