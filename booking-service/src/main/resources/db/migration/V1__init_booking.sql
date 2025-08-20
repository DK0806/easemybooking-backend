CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE daily_inventory (
  place_id UUID ,
  for_date DATE ,
  capacity INT ,
  reserved INT  DEFAULT 0,
  PRIMARY KEY (place_id, for_date)
);

CREATE TABLE bookings (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID ,
  place_id UUID ,
  for_date DATE ,
  quantity INT ,
  price_per_unit NUMERIC(10,2) ,
  total_amount NUMERIC(12,2) ,
  status VARCHAR(16) ,
  created_at TIMESTAMP  DEFAULT NOW(),
  updated_at TIMESTAMP  DEFAULT NOW()
);

CREATE INDEX idx_bookings_user ON bookings(user_id, created_at DESC);
CREATE INDEX idx_bookings_place_date ON bookings(place_id, for_date);
