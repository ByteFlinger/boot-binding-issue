package org.example.bindingIssue.service;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OtherService {

    @Setter
    private SomeService service;

    public String someMethod() {
        return String.format("ReadOnlyContext: %s", service.getContextSource().getReadOnlyContext().toString());
    }
}
