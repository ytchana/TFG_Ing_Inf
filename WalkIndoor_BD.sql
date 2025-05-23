PGDMP     *    4                }           walkindoor_tfg    15.12    15.12 w                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            !           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            "           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            #           1262    37397    walkindoor_tfg    DATABASE     t   CREATE DATABASE walkindoor_tfg WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'es-ES';
    DROP DATABASE walkindoor_tfg;
                postgres    false            $           0    0    walkindoor_tfg    DATABASE PROPERTIES     P   ALTER DATABASE walkindoor_tfg SET search_path TO '$user', 'public', 'topology';
                     postgres    false            	            2615    38479    topology    SCHEMA        CREATE SCHEMA topology;
    DROP SCHEMA topology;
                postgres    false            %           0    0    SCHEMA topology    COMMENT     9   COMMENT ON SCHEMA topology IS 'PostGIS Topology schema';
                   postgres    false    9                        3079    37399    postgis 	   EXTENSION     ;   CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
    DROP EXTENSION postgis;
                   false            &           0    0    EXTENSION postgis    COMMENT     ^   COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';
                        false    2                        3079    38891 	   pgrouting 	   EXTENSION     =   CREATE EXTENSION IF NOT EXISTS pgrouting WITH SCHEMA public;
    DROP EXTENSION pgrouting;
                   false    2            '           0    0    EXTENSION pgrouting    COMMENT     9   COMMENT ON EXTENSION pgrouting IS 'pgRouting Extension';
                        false    4                        3079    38480    postgis_topology 	   EXTENSION     F   CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;
 !   DROP EXTENSION postgis_topology;
                   false    9    2            (           0    0    EXTENSION postgis_topology    COMMENT     Y   COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';
                        false    3            �           1255    38888    detect_intersections()    FUNCTION     w  CREATE FUNCTION public.detect_intersections() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE my_intersectPointId BIGINT;
BEGIN
    -- 🔹 Insertar el punto de intersección con `LIMIT 1` para evitar múltiples filas
    INSERT INTO intersections (map_id, location, latitude, longitude)
    SELECT DISTINCT ON (ST_AsText(ST_Intersection(c1.path, c2.path))) 
        c1.map_id,
        ST_Intersection(c1.path, c2.path),
        ST_Y(ST_Intersection(c1.path, c2.path)),
        ST_X(ST_Intersection(c1.path, c2.path))
    FROM connections c1, connections c2
    WHERE ST_Intersects(c1.path, c2.path) 
    AND c1.id <> c2.id
    AND ST_GeometryType(ST_Intersection(c1.path, c2.path)) = 'ST_Point'
    ORDER BY ST_AsText(ST_Intersection(c1.path, c2.path))  -- 🔹 Ordenar antes de limitar resultados
    LIMIT 1
    RETURNING id INTO my_intersectPointId;

    RETURN NEW;
END;
$$;
 -   DROP FUNCTION public.detect_intersections();
       public          postgres    false            �           1255    40247    fill_intersection_connections()    FUNCTION     �  CREATE FUNCTION public.fill_intersection_connections() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- 🔹 Insertar conexiones con costos proporcionales a la distancia, evitando costos cero
    INSERT INTO intersection_connections (map_id, start_point, end_point, direction, cost, path)
    SELECT NEW.map_id, NEW.id, p.id, 100, 
        ST_Distance(NEW.location, p.location) * 5,  -- 🔹 Cálculo del costo basado en distancia
        ST_MakeLine(NEW.location, p.location)
    FROM points p
    WHERE ST_DWithin(NEW.location, p.location, 50)
    AND ST_Distance(NEW.location, p.location) > 0;  -- 🔹 Filtra entradas con distancia cero

    RETURN NEW;
END;
$$;
 6   DROP FUNCTION public.fill_intersection_connections();
       public          postgres    false            c           1255    38890    get_map_versions(integer)    FUNCTION     �  CREATE FUNCTION public.get_map_versions(map_id integer) RETURNS TABLE(version_old character varying, version_new character varying, old_desc text, new_desc text)
    LANGUAGE plpgsql
    AS $$
BEGIN
	RETURN QUERY
	SELECT v1.name AS version_old, v2.name AS version_new, v1.description AS old_desc, v2.description AS new_desc
	FROM maps_versions v1
	JOIN maps_versions v2 ON v1.map_id = v2.map_id AND v1.version_number = v2.version_number - 1
	WHERE v1.map_id = map_id;
END;
$$;
 7   DROP FUNCTION public.get_map_versions(map_id integer);
       public          postgres    false            �           1255    38886    update_direction_on_insert()    FUNCTION     �  CREATE FUNCTION public.update_direction_on_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Actualizar dirección de la línea en connections
    UPDATE connections
    SET direction = DEGREES(ST_Azimuth(
        (SELECT location FROM points WHERE id = NEW.start_point LIMIT 1),
        (SELECT location FROM points WHERE id = NEW.end_point LIMIT 1)
    ))
    WHERE id = NEW.id;

    -- Actualizar dirección en los puntos involucrados
    UPDATE points
    SET direction = DEGREES(ST_Azimuth(
        (SELECT location FROM points WHERE id = NEW.start_point LIMIT 1),
        (SELECT location FROM points WHERE id = NEW.end_point LIMIT 1)
    ))
    WHERE id = NEW.start_point;

    UPDATE points
    SET direction = DEGREES(ST_Azimuth(
        (SELECT location FROM points WHERE id = NEW.end_point LIMIT 1),
        (SELECT location FROM points WHERE id = NEW.start_point LIMIT 1)
    ))
    WHERE id = NEW.end_point;

    RETURN NEW;
END;
$$;
 3   DROP FUNCTION public.update_direction_on_insert();
       public          postgres    false            �            1259    38799    connections    TABLE     �   CREATE TABLE public.connections (
    id bigint NOT NULL,
    map_id bigint,
    start_point bigint NOT NULL,
    end_point bigint NOT NULL,
    path public.geometry(LineString,4326),
    direction double precision,
    cost double precision
);
    DROP TABLE public.connections;
       public         heap    postgres    false    2    2    2    2    2    2    2    2            �            1259    38798    connections_id_seq    SEQUENCE     �   CREATE SEQUENCE public.connections_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.connections_id_seq;
       public          postgres    false    238            )           0    0    connections_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.connections_id_seq OWNED BY public.connections.id;
          public          postgres    false    237            �            1259    40098    intersection_connections    TABLE     a  CREATE TABLE public.intersection_connections (
    id bigint NOT NULL,
    map_id integer NOT NULL,
    start_point bigint NOT NULL,
    end_point bigint NOT NULL,
    path public.geometry(LineString,4326) NOT NULL,
    direction double precision NOT NULL,
    cost double precision NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);
 ,   DROP TABLE public.intersection_connections;
       public         heap    postgres    false    2    2    2    2    2    2    2    2            �            1259    40097    intersection_connections_id_seq    SEQUENCE     �   CREATE SEQUENCE public.intersection_connections_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 6   DROP SEQUENCE public.intersection_connections_id_seq;
       public          postgres    false    248            *           0    0    intersection_connections_id_seq    SEQUENCE OWNED BY     c   ALTER SEQUENCE public.intersection_connections_id_seq OWNED BY public.intersection_connections.id;
          public          postgres    false    247            �            1259    40088    intersections    TABLE       CREATE TABLE public.intersections (
    id bigint NOT NULL,
    map_id integer NOT NULL,
    location public.geometry(Point,4326) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);
 !   DROP TABLE public.intersections;
       public         heap    postgres    false    2    2    2    2    2    2    2    2            �            1259    40087    intersections_id_seq    SEQUENCE     �   CREATE SEQUENCE public.intersections_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.intersections_id_seq;
       public          postgres    false    246            +           0    0    intersections_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.intersections_id_seq OWNED BY public.intersections.id;
          public          postgres    false    245            �            1259    38743    maps    TABLE     M  CREATE TABLE public.maps (
    id bigint NOT NULL,
    user_id bigint,
    name character varying(255) NOT NULL,
    description character varying(255),
    is_public boolean DEFAULT false,
    permissions jsonb DEFAULT '{}'::jsonb,
    shared_users integer[],
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
    DROP TABLE public.maps;
       public         heap    postgres    false            �            1259    38823    maps_comments    TABLE     �   CREATE TABLE public.maps_comments (
    id integer NOT NULL,
    map_id integer,
    user_id integer,
    comment text NOT NULL,
    posted_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
 !   DROP TABLE public.maps_comments;
       public         heap    postgres    false            �            1259    38822    maps_comments_id_seq    SEQUENCE     �   CREATE SEQUENCE public.maps_comments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.maps_comments_id_seq;
       public          postgres    false    240            ,           0    0    maps_comments_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.maps_comments_id_seq OWNED BY public.maps_comments.id;
          public          postgres    false    239            �            1259    38865    maps_history    TABLE     �   CREATE TABLE public.maps_history (
    id bigint NOT NULL,
    map_id bigint,
    user_id bigint,
    action character varying(255) NOT NULL,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
     DROP TABLE public.maps_history;
       public         heap    postgres    false            �            1259    38864    maps_history_id_seq    SEQUENCE     �   CREATE SEQUENCE public.maps_history_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.maps_history_id_seq;
       public          postgres    false    244            -           0    0    maps_history_id_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE public.maps_history_id_seq OWNED BY public.maps_history.id;
          public          postgres    false    243            �            1259    38742    maps_id_seq    SEQUENCE     �   CREATE SEQUENCE public.maps_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 "   DROP SEQUENCE public.maps_id_seq;
       public          postgres    false    232            .           0    0    maps_id_seq    SEQUENCE OWNED BY     ;   ALTER SEQUENCE public.maps_id_seq OWNED BY public.maps.id;
          public          postgres    false    231            �            1259    38760    maps_permissions    TABLE     �  CREATE TABLE public.maps_permissions (
    id integer NOT NULL,
    map_id integer,
    user_id integer,
    role character varying(20),
    can_view boolean DEFAULT false,
    can_edit boolean DEFAULT false,
    can_share boolean DEFAULT false,
    assigned_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT maps_permissions_role_check CHECK (((role)::text = ANY ((ARRAY['OWNER'::character varying, 'EDITOR'::character varying, 'VIEWER'::character varying])::text[])))
);
 $   DROP TABLE public.maps_permissions;
       public         heap    postgres    false            �            1259    38759    maps_permissions_id_seq    SEQUENCE     �   CREATE SEQUENCE public.maps_permissions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.maps_permissions_id_seq;
       public          postgres    false    234            /           0    0    maps_permissions_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.maps_permissions_id_seq OWNED BY public.maps_permissions.id;
          public          postgres    false    233            �            1259    38843    maps_versions    TABLE     v  CREATE TABLE public.maps_versions (
    id integer NOT NULL,
    map_id integer,
    user_id integer,
    version_number integer NOT NULL,
    name character varying(100),
    description text,
    is_public boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
 !   DROP TABLE public.maps_versions;
       public         heap    postgres    false            �            1259    38842    maps_versions_id_seq    SEQUENCE     �   CREATE SEQUENCE public.maps_versions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.maps_versions_id_seq;
       public          postgres    false    242            0           0    0    maps_versions_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.maps_versions_id_seq OWNED BY public.maps_versions.id;
          public          postgres    false    241            �            1259    38782    points    TABLE     �  CREATE TABLE public.points (
    id bigint NOT NULL,
    map_id bigint NOT NULL,
    location public.geometry(Point,4326),
    direction double precision,
    note character varying(255),
    flag character varying(255) DEFAULT NULL::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    latitude double precision,
    longitude double precision,
    CONSTRAINT points_flag_check CHECK (((flag)::text = ANY (ARRAY[('ENTRY'::character varying)::text, ('EXIT'::character varying)::text, ('INTEREST_POINT'::character varying)::text, ('DANGER'::character varying)::text, ('INFO'::character varying)::text])))
);
    DROP TABLE public.points;
       public         heap    postgres    false    2    2    2    2    2    2    2    2            �            1259    38781    points_id_seq    SEQUENCE     �   CREATE SEQUENCE public.points_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.points_id_seq;
       public          postgres    false    236            1           0    0    points_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.points_id_seq OWNED BY public.points.id;
          public          postgres    false    235            �            1259    38733    users    TABLE       CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
    DROP TABLE public.users;
       public         heap    postgres    false            �            1259    38732    users_id_seq    SEQUENCE     �   CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.users_id_seq;
       public          postgres    false    230            2           0    0    users_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
          public          postgres    false    229            ,           2604    39400    connections id    DEFAULT     p   ALTER TABLE ONLY public.connections ALTER COLUMN id SET DEFAULT nextval('public.connections_id_seq'::regclass);
 =   ALTER TABLE public.connections ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    237    238    238            7           2604    40423    intersection_connections id    DEFAULT     �   ALTER TABLE ONLY public.intersection_connections ALTER COLUMN id SET DEFAULT nextval('public.intersection_connections_id_seq'::regclass);
 J   ALTER TABLE public.intersection_connections ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    248    247    248            5           2604    40454    intersections id    DEFAULT     t   ALTER TABLE ONLY public.intersections ALTER COLUMN id SET DEFAULT nextval('public.intersections_id_seq'::regclass);
 ?   ALTER TABLE public.intersections ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    246    245    246                        2604    39250    maps id    DEFAULT     b   ALTER TABLE ONLY public.maps ALTER COLUMN id SET DEFAULT nextval('public.maps_id_seq'::regclass);
 6   ALTER TABLE public.maps ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    231    232    232            -           2604    38826    maps_comments id    DEFAULT     t   ALTER TABLE ONLY public.maps_comments ALTER COLUMN id SET DEFAULT nextval('public.maps_comments_id_seq'::regclass);
 ?   ALTER TABLE public.maps_comments ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    240    239    240            3           2604    39451    maps_history id    DEFAULT     r   ALTER TABLE ONLY public.maps_history ALTER COLUMN id SET DEFAULT nextval('public.maps_history_id_seq'::regclass);
 >   ALTER TABLE public.maps_history ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    243    244    244            $           2604    38763    maps_permissions id    DEFAULT     z   ALTER TABLE ONLY public.maps_permissions ALTER COLUMN id SET DEFAULT nextval('public.maps_permissions_id_seq'::regclass);
 B   ALTER TABLE public.maps_permissions ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    233    234    234            /           2604    38846    maps_versions id    DEFAULT     t   ALTER TABLE ONLY public.maps_versions ALTER COLUMN id SET DEFAULT nextval('public.maps_versions_id_seq'::regclass);
 ?   ALTER TABLE public.maps_versions ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    241    242    242            )           2604    39343 	   points id    DEFAULT     f   ALTER TABLE ONLY public.points ALTER COLUMN id SET DEFAULT nextval('public.points_id_seq'::regclass);
 8   ALTER TABLE public.points ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    236    235    236                       2604    39306    users id    DEFAULT     d   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    229    230    230                      0    38799    connections 
   TABLE DATA           `   COPY public.connections (id, map_id, start_point, end_point, path, direction, cost) FROM stdin;
    public          postgres    false    238   ��                 0    40098    intersection_connections 
   TABLE DATA           y   COPY public.intersection_connections (id, map_id, start_point, end_point, path, direction, cost, created_at) FROM stdin;
    public          postgres    false    248   U�                 0    40088    intersections 
   TABLE DATA           ^   COPY public.intersections (id, map_id, location, latitude, longitude, created_at) FROM stdin;
    public          postgres    false    246   ��                 0    38743    maps 
   TABLE DATA           p   COPY public.maps (id, user_id, name, description, is_public, permissions, shared_users, created_at) FROM stdin;
    public          postgres    false    232   ;�                 0    38823    maps_comments 
   TABLE DATA           P   COPY public.maps_comments (id, map_id, user_id, comment, posted_at) FROM stdin;
    public          postgres    false    240   ��                 0    38865    maps_history 
   TABLE DATA           P   COPY public.maps_history (id, map_id, user_id, action, "timestamp") FROM stdin;
    public          postgres    false    244   ַ                 0    38760    maps_permissions 
   TABLE DATA           q   COPY public.maps_permissions (id, map_id, user_id, role, can_view, can_edit, can_share, assigned_at) FROM stdin;
    public          postgres    false    234   �                 0    38843    maps_versions 
   TABLE DATA           �   COPY public.maps_versions (id, map_id, user_id, version_number, name, description, is_public, created_at, modified_at) FROM stdin;
    public          postgres    false    242   �                 0    38782    points 
   TABLE DATA           n   COPY public.points (id, map_id, location, direction, note, flag, created_at, latitude, longitude) FROM stdin;
    public          postgres    false    236   -�                 0    37721    spatial_ref_sys 
   TABLE DATA           X   COPY public.spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text) FROM stdin;
    public          postgres    false    219   M�                 0    38733    users 
   TABLE DATA           J   COPY public.users (id, username, email, password, created_at) FROM stdin;
    public          postgres    false    230   j�                 0    38482    topology 
   TABLE DATA           G   COPY topology.topology (id, name, srid, "precision", hasz) FROM stdin;
    topology          postgres    false    224    �                 0    38494    layer 
   TABLE DATA           �   COPY topology.layer (topology_id, layer_id, schema_name, table_name, feature_column, feature_type, level, child_id) FROM stdin;
    topology          postgres    false    225   =�       3           0    0    connections_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public.connections_id_seq', 4, true);
          public          postgres    false    237            4           0    0    intersection_connections_id_seq    SEQUENCE SET     M   SELECT pg_catalog.setval('public.intersection_connections_id_seq', 9, true);
          public          postgres    false    247            5           0    0    intersections_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.intersections_id_seq', 3, true);
          public          postgres    false    245            6           0    0    maps_comments_id_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('public.maps_comments_id_seq', 1, false);
          public          postgres    false    239            7           0    0    maps_history_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.maps_history_id_seq', 1, false);
          public          postgres    false    243            8           0    0    maps_id_seq    SEQUENCE SET     9   SELECT pg_catalog.setval('public.maps_id_seq', 3, true);
          public          postgres    false    231            9           0    0    maps_permissions_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.maps_permissions_id_seq', 1, false);
          public          postgres    false    233            :           0    0    maps_versions_id_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('public.maps_versions_id_seq', 1, false);
          public          postgres    false    241            ;           0    0    points_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.points_id_seq', 4, true);
          public          postgres    false    235            <           0    0    users_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.users_id_seq', 46, true);
          public          postgres    false    229            =           0    0    topology_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('topology.topology_id_seq', 1, false);
          topology          postgres    false    223            U           2606    39402    connections connections_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.connections
    ADD CONSTRAINT connections_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.connections DROP CONSTRAINT connections_pkey;
       public            postgres    false    238            d           2606    40425 6   intersection_connections intersection_connections_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY public.intersection_connections
    ADD CONSTRAINT intersection_connections_pkey PRIMARY KEY (id);
 `   ALTER TABLE ONLY public.intersection_connections DROP CONSTRAINT intersection_connections_pkey;
       public            postgres    false    248            b           2606    40456     intersections intersections_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.intersections
    ADD CONSTRAINT intersections_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.intersections DROP CONSTRAINT intersections_pkey;
       public            postgres    false    246            [           2606    38831     maps_comments maps_comments_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.maps_comments
    ADD CONSTRAINT maps_comments_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.maps_comments DROP CONSTRAINT maps_comments_pkey;
       public            postgres    false    240            `           2606    39453    maps_history maps_history_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.maps_history
    ADD CONSTRAINT maps_history_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.maps_history DROP CONSTRAINT maps_history_pkey;
       public            postgres    false    244            N           2606    38770 &   maps_permissions maps_permissions_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.maps_permissions
    ADD CONSTRAINT maps_permissions_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.maps_permissions DROP CONSTRAINT maps_permissions_pkey;
       public            postgres    false    234            K           2606    39252    maps maps_pkey 
   CONSTRAINT     L   ALTER TABLE ONLY public.maps
    ADD CONSTRAINT maps_pkey PRIMARY KEY (id);
 8   ALTER TABLE ONLY public.maps DROP CONSTRAINT maps_pkey;
       public            postgres    false    232            ^           2606    38853     maps_versions maps_versions_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.maps_versions
    ADD CONSTRAINT maps_versions_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.maps_versions DROP CONSTRAINT maps_versions_pkey;
       public            postgres    false    242            S           2606    39345    points points_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.points
    ADD CONSTRAINT points_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.points DROP CONSTRAINT points_pkey;
       public            postgres    false    236            G           2606    39340    users users_email_key 
   CONSTRAINT     Q   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);
 ?   ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_key;
       public            postgres    false    230            I           2606    39308    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            postgres    false    230            V           1259    39249    idx_connections_cost    INDEX     L   CREATE INDEX idx_connections_cost ON public.connections USING btree (cost);
 (   DROP INDEX public.idx_connections_cost;
       public            postgres    false    238            W           1259    39424    idx_connections_map_id    INDEX     P   CREATE INDEX idx_connections_map_id ON public.connections USING btree (map_id);
 *   DROP INDEX public.idx_connections_map_id;
       public            postgres    false    238            X           1259    39482    idx_connections_path    INDEX     K   CREATE INDEX idx_connections_path ON public.connections USING gist (path);
 (   DROP INDEX public.idx_connections_path;
       public            postgres    false    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    238            Y           1259    39248    idx_maps_comments_map_id    INDEX     T   CREATE INDEX idx_maps_comments_map_id ON public.maps_comments USING btree (map_id);
 ,   DROP INDEX public.idx_maps_comments_map_id;
       public            postgres    false    240            L           1259    39246    idx_maps_permissions_map_id    INDEX     Z   CREATE INDEX idx_maps_permissions_map_id ON public.maps_permissions USING btree (map_id);
 /   DROP INDEX public.idx_maps_permissions_map_id;
       public            postgres    false    234            \           1259    39247    idx_maps_versions_map_id    INDEX     T   CREATE INDEX idx_maps_versions_map_id ON public.maps_versions USING btree (map_id);
 ,   DROP INDEX public.idx_maps_versions_map_id;
       public            postgres    false    242            O           1259    39390    idx_points_location    INDEX     I   CREATE INDEX idx_points_location ON public.points USING gist (location);
 '   DROP INDEX public.idx_points_location;
       public            postgres    false    236    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2            P           1259    39374    idx_points_map_id    INDEX     F   CREATE INDEX idx_points_map_id ON public.points USING btree (map_id);
 %   DROP INDEX public.idx_points_map_id;
       public            postgres    false    236            Q           1259    40066    points_location_index    INDEX     K   CREATE INDEX points_location_index ON public.points USING gist (location);
 )   DROP INDEX public.points_location_index;
       public            postgres    false    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    2    236            t           2620    40067 (   connections trigger_detect_intersections    TRIGGER     �   CREATE TRIGGER trigger_detect_intersections AFTER INSERT ON public.connections FOR EACH ROW EXECUTE FUNCTION public.detect_intersections();
 A   DROP TRIGGER trigger_detect_intersections ON public.connections;
       public          postgres    false    1469    238            v           2620    40248 3   intersections trigger_fill_intersection_connections    TRIGGER     �   CREATE TRIGGER trigger_fill_intersection_connections AFTER INSERT ON public.intersections FOR EACH ROW EXECUTE FUNCTION public.fill_intersection_connections();
 L   DROP TRIGGER trigger_fill_intersection_connections ON public.intersections;
       public          postgres    false    1468    246            u           2620    38887 $   connections trigger_update_direction    TRIGGER     �   CREATE TRIGGER trigger_update_direction AFTER INSERT ON public.connections FOR EACH ROW EXECUTE FUNCTION public.update_direction_on_insert();
 =   DROP TRIGGER trigger_update_direction ON public.connections;
       public          postgres    false    238    1467            i           2606    39411 &   connections connections_end_point_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.connections
    ADD CONSTRAINT connections_end_point_fkey FOREIGN KEY (end_point) REFERENCES public.points(id);
 P   ALTER TABLE ONLY public.connections DROP CONSTRAINT connections_end_point_fkey;
       public          postgres    false    238    236    4691            j           2606    39425 #   connections connections_map_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.connections
    ADD CONSTRAINT connections_map_id_fkey FOREIGN KEY (map_id) REFERENCES public.maps(id) ON DELETE CASCADE;
 M   ALTER TABLE ONLY public.connections DROP CONSTRAINT connections_map_id_fkey;
       public          postgres    false    232    238    4683            k           2606    39438 (   connections connections_start_point_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.connections
    ADD CONSTRAINT connections_start_point_fkey FOREIGN KEY (start_point) REFERENCES public.points(id);
 R   ALTER TABLE ONLY public.connections DROP CONSTRAINT connections_start_point_fkey;
       public          postgres    false    4691    238    236            r           2606    40432 @   intersection_connections intersection_connections_end_point_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.intersection_connections
    ADD CONSTRAINT intersection_connections_end_point_fkey FOREIGN KEY (end_point) REFERENCES public.points(id);
 j   ALTER TABLE ONLY public.intersection_connections DROP CONSTRAINT intersection_connections_end_point_fkey;
       public          postgres    false    4691    236    248            s           2606    40457 B   intersection_connections intersection_connections_start_point_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.intersection_connections
    ADD CONSTRAINT intersection_connections_start_point_fkey FOREIGN KEY (start_point) REFERENCES public.intersections(id);
 l   ALTER TABLE ONLY public.intersection_connections DROP CONSTRAINT intersection_connections_start_point_fkey;
       public          postgres    false    4706    246    248            l           2606    39268 '   maps_comments maps_comments_map_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_comments
    ADD CONSTRAINT maps_comments_map_id_fkey FOREIGN KEY (map_id) REFERENCES public.maps(id) ON DELETE CASCADE;
 Q   ALTER TABLE ONLY public.maps_comments DROP CONSTRAINT maps_comments_map_id_fkey;
       public          postgres    false    240    232    4683            m           2606    39314 (   maps_comments maps_comments_user_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_comments
    ADD CONSTRAINT maps_comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
 R   ALTER TABLE ONLY public.maps_comments DROP CONSTRAINT maps_comments_user_id_fkey;
       public          postgres    false    4681    240    230            p           2606    39464 %   maps_history maps_history_map_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_history
    ADD CONSTRAINT maps_history_map_id_fkey FOREIGN KEY (map_id) REFERENCES public.maps(id) ON DELETE CASCADE;
 O   ALTER TABLE ONLY public.maps_history DROP CONSTRAINT maps_history_map_id_fkey;
       public          postgres    false    244    232    4683            q           2606    39473 &   maps_history maps_history_user_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_history
    ADD CONSTRAINT maps_history_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
 P   ALTER TABLE ONLY public.maps_history DROP CONSTRAINT maps_history_user_id_fkey;
       public          postgres    false    4681    230    244            f           2606    39253 -   maps_permissions maps_permissions_map_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_permissions
    ADD CONSTRAINT maps_permissions_map_id_fkey FOREIGN KEY (map_id) REFERENCES public.maps(id) ON DELETE CASCADE;
 W   ALTER TABLE ONLY public.maps_permissions DROP CONSTRAINT maps_permissions_map_id_fkey;
       public          postgres    false    232    234    4683            g           2606    39309 .   maps_permissions maps_permissions_user_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_permissions
    ADD CONSTRAINT maps_permissions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
 X   ALTER TABLE ONLY public.maps_permissions DROP CONSTRAINT maps_permissions_user_id_fkey;
       public          postgres    false    234    230    4681            e           2606    39329    maps maps_user_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps
    ADD CONSTRAINT maps_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
 @   ALTER TABLE ONLY public.maps DROP CONSTRAINT maps_user_id_fkey;
       public          postgres    false    230    232    4681            n           2606    39273 '   maps_versions maps_versions_map_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_versions
    ADD CONSTRAINT maps_versions_map_id_fkey FOREIGN KEY (map_id) REFERENCES public.maps(id) ON DELETE CASCADE;
 Q   ALTER TABLE ONLY public.maps_versions DROP CONSTRAINT maps_versions_map_id_fkey;
       public          postgres    false    242    4683    232            o           2606    39319 (   maps_versions maps_versions_user_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.maps_versions
    ADD CONSTRAINT maps_versions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
 R   ALTER TABLE ONLY public.maps_versions DROP CONSTRAINT maps_versions_user_id_fkey;
       public          postgres    false    242    230    4681            h           2606    39375    points points_map_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.points
    ADD CONSTRAINT points_map_id_fkey FOREIGN KEY (map_id) REFERENCES public.maps(id) ON DELETE CASCADE;
 C   ALTER TABLE ONLY public.points DROP CONSTRAINT points_map_id_fkey;
       public          postgres    false    232    236    4683               �   x��ϻ��0E�X*�x�S���b��cmO>�LH���B�r�qc9��:�r��'H��F���:T{��X�t�@Z��.�����C��6I��@�Ǖ��������nB���և���B��܍<��<��C~+��:����5��6y��)O��!�3u�aF�'�'���
����IZ��~�Z�]�c         '  x���In� �}�^ �?������Q��YU�U`��OO\� �1�!�s9F��(�� ��腺� "�L��T���dl�h�{X���-Ȕ1Q����.yg��EW$y�ѥ���[�{d���^�{����j�al3��S;b�&��Z �K��5Y_(��ӏ� Hȕg&�YPgo^�HC$�H��F��r�:P��,��c�a����t��Q0���	NQ>��4��G/u���v����Y��H�J~)�*j��Ե��I�ǥ攎3�綮�p��         �   x�mϻq�0И��h���eEV���Ɨ
H��7n�O+�*m��3N�A�g�)�/��1i�7!QM ~�����G���]^����X|2��1o\��@Ƒ�P�|�ص���=r�w)�8��5MA�,����"$!SB�/��� �:         n   x���1�0 ��~�?@d����XR6*Tʄ�wf�2�tϲ�*����
<�o������*3?PVk�1b�<�!�����I�hn�q�;lo�:71e�ǀ�'� A�            x������ � �            x������ � �            x������ � �            x������ � �           x�u��nAE�ٯ���?Ʀ�yuI�"�YA!!�(��ِ2�۸;�^ �l����7�(Vy��!�T5)��QD%dgQ��z;|���5��B�$3Ȍ�cߑDqq��ɒ�1�O���*#wV\Z�\'��FRpaM$�u=�N�g��CdT3�d=�g��#4+c8aץ')�i�k̢=!e��?/�����D@�<���"e��
�n�e�L��a�R��4��G�'��֯��sa����f�f��>N���9ke            x������ � �         �   x���A�0���0�R�-'0nMHȤN���*��^L@M� ��4�/?��CS��C�t��\S�m���!!W`]l8����S�U=��5�!���K�X)�a:ղ�~�;G�C���}'����X%i��b5?�f6�Z��h𓣑-2_��wc}���O�            x������ � �            x������ � �     