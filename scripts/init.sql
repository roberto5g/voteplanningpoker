-- Usuário
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    can_reveal_vote BOOLEAN NOT NULL DEFAULT false,
    is_admin BOOLEAN NOT NULL DEFAULT false
);

-- Sala (Room)
CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL,
    creator_id UUID NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- Tabela intermediária: participantes
CREATE TABLE room_participants (
    room_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela intermediária: usuários autorizados a revelar votos
CREATE TABLE room_reveal_authorized_users (
    room_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Votes permitidos na sala (allowedVotes)
CREATE TABLE room_allowed_votes (
    room_id UUID NOT NULL,
    vote_value INTEGER NOT NULL,
    PRIMARY KEY (room_id, vote_value),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Tópico
CREATE TABLE topics (
    id UUID PRIMARY KEY,
    room_id UUID UNIQUE, -- cada sala tem no máximo 1 tópico ativo (conforme seu modelo atual)
    title VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    votes_revealed BOOLEAN NOT NULL DEFAULT false,
    average NUMERIC(5,2),
    suggested INTEGER,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Voto
CREATE TABLE votes (
    id SERIAL PRIMARY KEY,
    topic_id UUID NOT NULL,
    user_name VARCHAR(255) NOT NULL, -- nome do usuário no voto
    vote_value INTEGER NOT NULL,
    FOREIGN KEY (topic_id) REFERENCES topics(id)
);
