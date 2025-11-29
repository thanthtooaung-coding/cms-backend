-- Fix existing Booking System records to use BOOKING
UPDATE "Page_Request" 
SET request_type = 'BOOKING' 
WHERE request_type = 'Booking System';

-- Verify the update
SELECT id, request_type, title FROM "Page_Request" WHERE request_type = 'BOOKING';
