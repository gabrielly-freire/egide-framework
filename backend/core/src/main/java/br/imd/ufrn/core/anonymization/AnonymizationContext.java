package br.imd.ufrn.core.anonymization;

public record AnonymizationContext(
        boolean anonymous,
        String type
) {}
