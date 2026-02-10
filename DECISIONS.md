# MarkNow – Technical Decisions

## Project Identity
- Name: MarkNow
- Purpose: Appointment scheduling using time slots for businesses

## Backend
- Language: Java 17
- Framework: Quarkus
- Reason: Robustness, transactions, domain rules

## Web
- Framework: Next.js (React)
- Reason: SEO support and scalability

## Mobile
- Framework: Flutter
- Reason: Single codebase and fast delivery

## Database
- PostgreSQL
- Reason: Integrity, concurrency control, timestamptz support

## Domain Rules
- Timezone: America/Sao_Paulo
- Slot-based scheduling
- Atomic booking (no double booking at DB level)

## Scope – Phase 1
- Single company
- Single service
- Fixed duration
- No payments
- No notifications

