CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO questions (question) VALUES
    ('What are your opening hours?'),
    ('How do I reset my password?'),
    ('Where is my order?'),
    ('Do you offer refunds?'),
    ('How can I contact support?');
