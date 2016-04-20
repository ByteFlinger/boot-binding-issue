package org.example.bindingIssue.service;

import lombok.Data;
import lombok.NonNull;
import org.springframework.ldap.core.ContextSource;

@Data
public class SomeService {

    @NonNull
    private final ContextSource contextSource;
}
