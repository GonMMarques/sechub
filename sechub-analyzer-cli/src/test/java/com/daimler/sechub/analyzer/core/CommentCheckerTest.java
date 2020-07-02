package com.daimler.sechub.analyzer.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class CommentCheckerTest {
    private static final String NOSECHUB = "NOSECHUB";
    private static final String NOSECHUB_END = "END-NOSECHUB";
    
    @Test 
    public void test_of() {
        /* execute */
        CommentChecker commentCheckerInstance = CommentChecker.of(NOSECHUB, NOSECHUB_END);
        
        /* test */
        assertThat(commentCheckerInstance, is(not(nullValue())));
        assertThat(commentCheckerInstance.getNoSecHubLabel(), is(NOSECHUB));
    }
}


