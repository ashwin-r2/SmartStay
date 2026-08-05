# Sequence Diagrams

## 1. Registration & JWT login

```mermaid
sequenceDiagram
    actor Guest
    participant FE as React Frontend
    participant Auth as AuthController
    participant Svc as AuthService
    participant Repo as UserRepository
    participant DB as PostgreSQL
    participant JWT as JwtService

    Guest->>FE: Fill registration form, submit
    FE->>Auth: POST /api/auth/register
    Auth->>Svc: register(RegisterRequest)
    Svc->>Repo: existsByEmail(email)
    Repo->>DB: SELECT
    DB-->>Repo: not found
    Svc->>Svc: BCrypt.encode(password)
    Svc->>Repo: save(User)
    Repo->>DB: INSERT
    Svc->>JWT: generateAccessToken(user)
    Svc->>JWT: generateRefreshToken(user)
    Svc->>Repo: save(RefreshToken hash)
    Svc-->>Auth: AuthResponse(accessToken, refreshToken, user)
    Auth-->>FE: 201 Created
    FE->>FE: store tokens (localStorage), set AuthContext.user
    FE-->>Guest: redirected to Home, logged in

    Note over FE,Auth: Later requests attach<br/>Authorization: Bearer &lt;accessToken&gt;
    FE->>Auth: (any protected request)
    Auth->>JWT: JwtAuthenticationFilter validates token
    JWT-->>Auth: principal = User, authorities = [ROLE_USER]
```

## 2. Search & book a property

```mermaid
sequenceDiagram
    actor Guest
    participant FE as React Frontend
    participant PC as PropertyController
    participant PS as PropertyService
    participant BC as BookingController
    participant BS as BookingService
    participant DB as PostgreSQL

    Guest->>FE: Search city + dates + guests
    FE->>PC: GET /api/properties/search?city=...&checkIn=...&checkOut=...
    PC->>PS: search(criteria, pageable)
    PS->>DB: SELECT ... WHERE status=ACTIVE AND NOT EXISTS (overlapping availability_blocks)
    DB-->>PS: matching properties
    PS-->>PC: Page~PropertySummaryResponse~
    PC-->>FE: 200 OK
    FE-->>Guest: render results grid

    Guest->>FE: Open property, pick dates, click "Book now"
    FE->>BC: POST /api/bookings {propertyId, checkIn, checkOut, guestsCount}
    BC->>BS: create(guestId, request)
    BS->>PS: getEntityById(propertyId)
    BS->>PS: isAvailable(propertyId, checkIn, checkOut)
    PS->>DB: SELECT overlapping availability_blocks
    DB-->>PS: none found
    PS-->>BS: true
    BS->>BS: compute nights, totalPrice
    BS->>DB: INSERT INTO bookings (status = CONFIRMED)
    BS->>PS: blockForBooking(propertyId, checkIn, checkOut, bookingId)
    PS->>DB: INSERT INTO availability_blocks (reason = BOOKED)
    BS-->>BC: BookingResponse
    BC-->>FE: 201 Created
    FE-->>Guest: "Booking confirmed!"
```

## 3. AI Chat Assistant (multi-turn)

```mermaid
sequenceDiagram
    actor Guest
    participant FE as React Frontend (AiChatWidget)
    participant AC as AiController
    participant CS as ChatAssistantAiService
    participant Repo as AiChatMessageRepository
    participant LLM as ChatLanguageModel (LangChain4j)
    participant OpenAI as OpenAI API
    participant Log as AiUsageLogger

    Guest->>FE: Types a message, hits send
    FE->>AC: POST /api/ai/chat {sessionId?, message}
    AC->>CS: chat(userId, request)
    CS->>Repo: findBySessionIdOrderByCreatedAtAsc(sessionId)
    Repo-->>CS: prior turns (if any)
    CS->>CS: build [SystemMessage, ...history, UserMessage(new)]
    CS->>LLM: generate(messages)
    LLM->>OpenAI: chat completion request
    OpenAI-->>LLM: completion + token usage
    LLM-->>CS: Response~AiMessage~
    CS->>Log: logSuccess(CHAT_ASSISTANT, tokens, latency)
    CS->>Repo: save(USER turn), save(ASSISTANT turn)
    CS-->>AC: ChatResponse(sessionId, reply)
    AC-->>FE: 200 OK
    FE-->>Guest: renders assistant reply bubble

    Note over LLM,OpenAI: If OPENAI_API_KEY is unset,<br/>a NoopChatLanguageModel throws immediately —<br/>CS logs failure and returns HTTP 503 with a friendly message.
```

## 4. AI Review Summarization

```mermaid
sequenceDiagram
    actor Guest
    participant FE as React Frontend (PropertyDetails)
    participant AC as AiController
    participant RS as ReviewSummaryAiService
    participant RevSvc as ReviewService
    participant Client as ReviewSummaryAiClient (LangChain4j @AiService)
    participant OpenAI as OpenAI API
    participant Log as AiUsageLogger

    Guest->>FE: Clicks "AI Summary" on a property's reviews
    FE->>AC: GET /api/ai/reviews/{propertyId}/summary
    AC->>RS: summarize(propertyId, userId)
    RS->>RevSvc: getCommentsForProperty(propertyId, 50)
    RevSvc-->>RS: List~String~ review comments
    alt no written reviews yet
        RS-->>AC: 400 Bad Request ("no reviews to summarize")
    else has reviews
        RS->>Client: summarize(reviewsText)
        Client->>OpenAI: chat completion request
        OpenAI-->>Client: "Overall: ... Pros: ... Cons: ... Verdict: ..."
        Client-->>RS: summary text
        RS->>Log: logSuccess(REVIEW_SUMMARY, latency)
        RS-->>AC: ReviewSummaryResponse
        AC-->>FE: 200 OK
        FE-->>Guest: renders summary card
    end
```
