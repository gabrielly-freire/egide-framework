export interface AttachmentResponse {
  id: number;
  manifestationId: number;
  fileName: string;
  contentType: string | null;
  fileSize: number;
}
