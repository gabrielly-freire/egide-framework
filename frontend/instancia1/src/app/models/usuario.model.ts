export type Role = 'REMONSTRANT' | 'LISTENER' | 'MANAGER' | 'GENERAL_LISTENER' | 'ADMIN';

// Payload de POST /v1/users (CreateUserRequest do backend) — não há endpoints de update/delete.
export interface User {
  email: string;
  username: string;
  password: string;
  name: string;
  role: Role;
  departmentId: number | null;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  username: string;
  role: Role;
  departmentId: number | null;
  departmentName: string | null;
}
