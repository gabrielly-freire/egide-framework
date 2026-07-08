export interface Department {
  id: number;
  name: string;
  acronym: string | null;
}

export interface CreateDepartmentRequest {
  name: string;
  acronym?: string | null;
}
