package com.staysmart.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryingChatLanguageModelTest {

    private static final List<ChatMessage> MESSAGES = List.of(UserMessage.from("hi"));

    @Mock
    private ChatLanguageModel delegate;

    private static RuntimeException unavailable() {
        return new RuntimeException("An error occurred when calling the Gemini API endpoint.",
                new RuntimeException("UNAVAILABLE (code 503) This model is currently experiencing high demand."));
    }

    @Test
    void retriesOn503ThenSucceeds() {
        Response<AiMessage> ok = Response.from(AiMessage.from("OK"));
        when(delegate.generate(anyList())).thenThrow(unavailable()).thenThrow(unavailable()).thenReturn(ok);

        Response<AiMessage> result = new RetryingChatLanguageModel(delegate, 3, 0).generate(MESSAGES);

        assertThat(result.content().text()).isEqualTo("OK");
        verify(delegate, times(3)).generate(anyList());
    }

    @Test
    void givesUpAfterMaxRetries() {
        when(delegate.generate(anyList())).thenThrow(unavailable());

        assertThatThrownBy(() -> new RetryingChatLanguageModel(delegate, 2, 0).generate(MESSAGES))
                .hasRootCauseMessage("UNAVAILABLE (code 503) This model is currently experiencing high demand.");
        verify(delegate, times(3)).generate(anyList());
    }

    @Test
    void doesNotRetryNonTransientErrors() {
        when(delegate.generate(anyList())).thenThrow(new RuntimeException("UNAUTHENTICATED (code 401) bad key"));

        assertThatThrownBy(() -> new RetryingChatLanguageModel(delegate, 3, 0).generate(MESSAGES))
                .hasMessageContaining("401");
        verify(delegate, times(1)).generate(anyList());
    }
}
