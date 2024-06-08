--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3
-- Dumped by pg_dump version 16.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.events
(
    id                        bigint                 NOT NULL,
    name                      character varying(100) NOT NULL,
    date                      date                   NOT NULL,
    available_attendees_count integer                NOT NULL,
    category                  character varying(20)  NOT NULL,
    description               character varying(500) NOT NULL,
    created_by_id             uuid,
    last_modified_by_id       uuid,
    date_created              timestamp with time zone,
    date_last_modified        timestamp with time zone
);


--
-- Name: events_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.events_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.events_id_seq OWNED BY public.events.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flyway_schema_history
(
    installed_rank integer                                   NOT NULL,
    version        character varying(50),
    description    character varying(200)                    NOT NULL,
    type           character varying(20)                     NOT NULL,
    script         character varying(1000)                   NOT NULL,
    checksum       integer,
    installed_by   character varying(100)                    NOT NULL,
    installed_on   timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer                                   NOT NULL,
    success        boolean                                   NOT NULL
);


--
-- Name: tickets; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tickets
(
    id                  bigint  NOT NULL,
    attendees_count     integer NOT NULL,
    event_id            bigint  NOT NULL,
    created_by_id       uuid,
    last_modified_by_id uuid,
    date_created        timestamp with time zone,
    date_last_modified  timestamp with time zone
);


--
-- Name: tickets_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tickets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tickets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tickets_id_seq OWNED BY public.tickets.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users
(
    id                 uuid NOT NULL,
    name               character varying(100),
    email              character varying(250),
    password           character varying(500),
    date_created       timestamp with time zone,
    date_last_modified timestamp with time zone
);


--
-- Name: events id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ALTER COLUMN id SET DEFAULT nextval('public.events_id_seq'::regclass);


--
-- Name: tickets id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ALTER COLUMN id SET DEFAULT nextval('public.tickets_id_seq'::regclass);


--
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.events (id, name, date, available_attendees_count, category, description, created_by_id,
                    last_modified_by_id, date_created, date_last_modified) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by,
                                   installed_on, execution_time, success) FROM stdin;
1	1	users	SQL	V1__users.sql	146035626	test	2024-06-08 12:07:43.743678	9	t
2	2	events	SQL	V2__events.sql	412613096	test	2024-06-08 12:07:43.766934	9	t
3	3	tickets	SQL	V3__tickets.sql	28774	test	2024-06-08 12:07:43.783613	5	t
\.


--
-- Data for Name: tickets; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tickets (id, attendees_count, event_id, created_by_id, last_modified_by_id, date_created,
                     date_last_modified) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, name, email, password, date_created, date_last_modified) FROM stdin;
8561bdd3-078f-4592-bd9d-18f1f8219e46	Test	test@example.org	$2a$10$O.zMErgpZX0mJ2OR0NYpoOaQw5nWqhr6adEsaNJis8IDFMTw4YN0K	2024-06-08 09:09:58.687904+00	\N
\.


--
-- Name: events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.events_id_seq', 1, false);


--
-- Name: tickets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tickets_id_seq', 1, false);


--
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: tickets tickets_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_events_category; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_events_category ON public.events USING btree (category);


--
-- Name: idx_events_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_events_date ON public.events USING btree (date);


--
-- Name: idx_events_date_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_events_date_created ON public.events USING btree (date_created);


--
-- Name: idx_events_date_last_modified; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_events_date_last_modified ON public.events USING btree (date_last_modified);


--
-- Name: idx_events_description; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_events_description ON public.events USING btree (description);


--
-- Name: idx_events_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_events_name ON public.events USING btree (name);


--
-- Name: idx_tickets_date_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tickets_date_created ON public.tickets USING btree (date_created);


--
-- Name: idx_tickets_date_last_modified; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tickets_date_last_modified ON public.tickets USING btree (date_last_modified);


--
-- Name: idx_users_date_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_date_created ON public.users USING btree (date_created);


--
-- Name: idx_users_date_last_modified; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_date_last_modified ON public.users USING btree (date_last_modified);


--
-- Name: idx_users_email; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_email ON public.users USING btree (email);


--
-- Name: idx_users_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_name ON public.users USING btree (name);


--
-- Name: idx_users_password; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_password ON public.users USING btree (password);


--
-- Name: events events_created_by_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_created_by_id_fkey FOREIGN KEY (created_by_id) REFERENCES public.users (id) ON DELETE SET NULL;


--
-- Name: events events_last_modified_by_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_last_modified_by_id_fkey FOREIGN KEY (last_modified_by_id) REFERENCES public.users (id) ON DELETE SET NULL;


--
-- Name: tickets tickets_created_by_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_created_by_id_fkey FOREIGN KEY (created_by_id) REFERENCES public.users (id) ON DELETE SET NULL;


--
-- Name: tickets tickets_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events (id) ON DELETE CASCADE;


--
-- Name: tickets tickets_last_modified_by_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_last_modified_by_id_fkey FOREIGN KEY (last_modified_by_id) REFERENCES public.users (id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

