package br.imd.ufrn.attachment;

public record AttachmentResponse(
        Long id,
        Long manifestationId,
        String fileName,
        String contentType,
        long fileSize
) {}
