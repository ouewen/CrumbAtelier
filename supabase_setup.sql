-- ============================================================
-- CRUMB ATELIER — Supabase Setup SQL
-- Run once in: Supabase Dashboard > SQL Editor > New Query
-- ============================================================

-- 1. PROFILES TABLE
create table if not exists public.profiles (
    id         uuid        primary key references auth.users(id) on delete cascade,
    name       text        not null default '',
    email      text        not null default '',
    role       text        not null default 'VIEWER' check (role in ('ADMIN','VIEWER')),
    created_at timestamptz default now()
);
alter table public.profiles enable row level security;
create policy "Own profile read"   on public.profiles for select using (auth.uid() = id);
create policy "Own profile update" on public.profiles for update using (auth.uid() = id);

-- 2. AUTO-CREATE PROFILE ON SIGNUP
create or replace function public.handle_new_user()
returns trigger language plpgsql security definer set search_path = public as $$
begin
    insert into public.profiles(id, name, email, role)
    values (new.id,
            coalesce(new.raw_user_meta_data->>'name','User'),
            coalesce(new.email,''),
            coalesce(new.raw_user_meta_data->>'role','VIEWER'));
    return new;
end;
$$;
drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created after insert on auth.users
    for each row execute procedure public.handle_new_user();

-- 3. RECIPES TABLE
create table if not exists public.recipes (
    id          bigserial   primary key,
    title       text        not null,
    description text        not null default '',
    ingredients text        not null,
    steps       text        not null,
    image_url   text,
    created_by  uuid        references auth.users(id) on delete set null,
    created_at  timestamptz default now()
);
alter table public.recipes enable row level security;
create policy "Anyone reads recipes"  on public.recipes for select  using (true);
create policy "Admin inserts recipes" on public.recipes for insert  with check (exists (select 1 from public.profiles where id = auth.uid() and role = 'ADMIN'));
create policy "Admin updates recipes" on public.recipes for update  using        (exists (select 1 from public.profiles where id = auth.uid() and role = 'ADMIN'));
create policy "Admin deletes recipes" on public.recipes for delete  using        (exists (select 1 from public.profiles where id = auth.uid() and role = 'ADMIN'));

-- 4. FAVORITES TABLE
create table if not exists public.favorites (
    id        bigserial primary key,
    user_id   uuid   not null references auth.users(id) on delete cascade,
    recipe_id bigint not null references public.recipes(id) on delete cascade,
    created_at timestamptz default now(),
    unique(user_id, recipe_id)
);
alter table public.favorites enable row level security;
create policy "Own favorites" on public.favorites for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- 5. REALTIME
alter publication supabase_realtime add table public.recipes;
alter publication supabase_realtime add table public.favorites;

-- 6. SEED RECIPES (run after creating an admin user)
insert into public.recipes(title, description, ingredients, steps, image_url) values
('Sourdough Country Loaf','A classic open-crumb sourdough with a golden crackling crust.',
 '500g bread flour\n375g water\n10g salt\n100g active sourdough starter',
 '1. Mix flour and water; rest 30 min.\n2. Add starter and salt; incorporate fully.\n3. Stretch-and-fold 4 times every 30 min.\n4. Bulk ferment 4–6 hours until doubled.\n5. Shape into boule; cold proof overnight.\n6. Bake in Dutch oven 250°C: 20 min covered, 25 min uncovered.',
 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800'),
('Kouign-Amann','Brittany''s butter-laminated, deeply caramelised pastry.',
 '300g all-purpose flour\n7g instant yeast\n150ml warm water\n5g salt\n200g cold unsalted butter\n150g granulated sugar',
 '1. Knead flour, yeast, water, salt 8 min; prove 1 hour.\n2. Roll; laminate cold butter.\n3. Sprinkle sugar, fold twice.\n4. Press into buttered 22cm tin; prove 30 min.\n5. Bake 200°C for 30–35 min until deep caramel.',
 'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800'),
('Cardamom Brioche Rolls','Pillowy enriched dough perfumed with cardamom and vanilla.',
 '400g bread flour\n50g sugar\n7g instant yeast\n1 tsp cardamom\n3 eggs\n150ml warm milk\n150g softened butter',
 '1. Mix dry ingredients; add eggs and milk.\n2. Incorporate butter until windowpane achieved.\n3. Bulk ferment 2 hours.\n4. Shape 12 rolls; prove 1 hour.\n5. Brush egg wash; bake 180°C 18–22 min.',
 'https://images.unsplash.com/photo-1586985289688-ca3cf47d3e6e?w=800'),
('Frangipane Tart','Buttery shortcrust filled with almond cream and seasonal fruit.',
 'Pastry: 200g flour, 100g butter, 2 tbsp icing sugar, 1 egg yolk\nFrangipane: 120g butter, 120g sugar, 2 eggs, 120g almond flour\nTopping: seasonal stone fruit',
 '1. Make shortcrust; chill 30 min; blind bake 190°C 15 min.\n2. Beat butter and sugar; add eggs; fold in almond flour.\n3. Spread frangipane; arrange fruit.\n4. Bake 180°C 25–30 min until set.',
 'https://images.unsplash.com/photo-1519915028121-7d3463d20b13?w=800'),
('Earl Grey Madeleines','Delicate shell cakes infused with bergamot and lemon.',
 '90g plain flour\n1 tsp baking powder\n100g caster sugar\n2 eggs\n2 tbsp brewed Earl Grey\nZest of 1 lemon\n80g browned butter',
 '1. Steep Earl Grey; cool.\n2. Whisk eggs and sugar until ribbony.\n3. Fold in flour, baking powder, tea, zest, butter.\n4. Chill batter 1 hour.\n5. Fill greased moulds 3/4 full; bake 210°C 11–12 min.',
 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800');

-- 7. TO PROMOTE A USER TO ADMIN:
-- update public.profiles set role = 'ADMIN' where email = 'your@email.com';
