# PV Hearing Radius & Flat Ring
mod นี้ใช้ ai ในการช่วยสร้าง สามารถนำไปดัดแปลงได้
Forge 1.20.1 / Plasmo Voice 2.1.13 client addon

## พฤติกรรม

1. **วงไมค์ของ Plasmo Voice**
   - ไม่ลบการแสดงระยะไมค์อีกต่อไป
   - เปลี่ยนเฉพาะรูปร่างจากโดมเขียวเป็น **วงกลมแบนเส้นบาง**
   - ใช้ระยะไมค์จริงจาก Plasmo Voice
   - มีพฤติกรรมชั่วคราวเหมือนตัวแสดงผลเดิม: ขึ้นเมื่อ Plasmo Voice เรียกแสดงระยะ แล้วค่อยหาย/จาง
   - `/voicering toggle` เปิด/ปิดวงไมค์แบบแบน

2. **วงระยะที่เราได้ยิน + การตั้งค่าทั้งหมด**
   - ตั้งค่าทั้งหมดของแอดออนนี้อยู่ใน `Plasmo Voice -> Settings -> Add-ons` แล้ว ได้แก่:
     `Hearing distance` (1-128 blocks), `Limit hearing distance`, `Flat ring indicator`,
     `Ring opacity`, สี ring หู (Red/Green/Blue แยก 3 แถบ), `Custom mic ring color`,
     และสี ring ไมค์ (Red/Green/Blue แยก 3 แถบ)
   - เหตุผลที่สีแยกเป็น R/G/B คนละแถบ เพราะหน้าตั้งค่าของ Plasmo Voice เองไม่มีตัวเลือกสีแบบจิ้ม
     มีแค่ slider/toggle/dropdown เท่านั้น — ถ้าอยากจิ้มเลือกสีแบบเห็นภาพ ให้ใช้หน้าต่าง GUI ของแอดออน
     (ข้อ 4) ซึ่งอ่าน/เขียนค่าตัวเดียวกันนี้ ปรับที่ไหนก็ตรงกันทั้งสองที่

3. **คำสั่ง**
   - `/hearingdistance` ดูค่าปัจจุบัน
   - `/hearingdistance <1-128>` ตั้งระยะ
   - `/hearingdistance toggle` เปิด/ปิดการจำกัดเสียง
   - `/voicering toggle` เปิด/ปิดวงไมค์แบบแบน
   - `/voicering color <0xRRGGBB>` ตั้งสีวงหู
   - `/voicering alpha <0-255>` ตั้งความทึบ
   - `/hearingring` หรือ `/hearingdistance gui` หรือ `/voicering gui` เปิดหน้าต่างตั้งค่าในเกม

4. **หน้าต่างตั้งค่าในเกม (GUI)**
   - เปิดด้วยคำสั่งด้านบน — ปุ่มทุกปุ่มในนี้อ่าน/เขียนค่าเดียวกับหน้า Add-ons ของ Plasmo Voice
     (ข้อ 2) ไม่ได้แยกค่ากันคนละที่
   - ปุ่ม `-5 / -1 / +1 / +5` ปรับระยะที่ได้ยิน (hearing distance) แบบกดทันที
   - ปุ่มเปิด/ปิด `Limit hearing distance` และ `Flat ring indicator`
   - ปุ่ม **"Hearing ring color"** เปิดหน้าต่างเลือกสีแบบจิ้มเอา (สี่เหลี่ยม saturation/value +
     แถบสี hue + ช่องกรอกฮ็กซ์) สำหรับ **วงระยะที่ได้ยิน (ring หู)**
   - ปุ่มเปิด/ปิด **"Custom mic ring color"** — เมื่อเปิดไว้ วงไมค์จะใช้สีที่ตั้งเองแทนสีเดิมของ
     Plasmo Voice
   - ปุ่ม **"Mic ring color"** เปิดหน้าต่างเลือกสีแบบเดียวกัน สำหรับ **วงไมค์ (ring ไมค์)**
   - ลากนิ้ว/เมาส์บนสี่เหลี่ยมหรือแถบสีเพื่อเปลี่ยนสีแบบเรียลไทม์ พรีวิวเห็นผลทันทีในโลกเกมด้านหลัง
   - ปุ่ม "ยกเลิก" คืนค่าสีเดิมก่อนเปิดหน้าต่าง, ปุ่ม "บันทึก" ปิดหน้าต่างและเก็บค่าที่เลือกไว้
   - ปุ่ม **"Increase distance: ..." / "Decrease distance: ..."** ตั้งปุ่มลัด (keybind) ได้ตรงนี้เลย
     ไม่ต้องออกไปที่เมนู Controls: **คลิกซ้าย** แล้วกดปุ่มที่ต้องการผูก, **คลิกขวา** เพื่อล้างปุ่มที่ผูกไว้,
     กด **Esc** ระหว่างรอกดปุ่มเพื่อยกเลิกโดยไม่เปลี่ยนค่า

5. **ปุ่มลัด (Keybind)**
   - ตั้งได้ 2 ที่ ค่าเดียวกัน: ในหน้าต่าง GUI ของแอดออนโดยตรง (ข้อ 4) หรือที่
     Options -> Controls -> Key Binds -> หมวด "PV Hearing Radius & Flat Ring"
   - ค่าเริ่มต้นไม่ได้ผูกปุ่มไว้ (unbound) ต้องเข้าไปตั้งเอง
   - `Increase hearing distance` / `Decrease hearing distance` ปรับระยะทีละ 1
   - กด **Shift ค้าง** ระหว่างกดปุ่มลัด จะปรับทีละ 5 แทน


## Keybind 1.7.0

ตั้งปุ่มได้โดยตรงที่ Plasmo Voice → Settings → Add-ons:
- Open GUI key
- Increase distance key
- Decrease distance key

ค่าที่เลือกถูกอ่านจริงทุก client tick และใช้ปุ่มนั้นเปิด GUI/เพิ่ม/ลดระยะ ไม่ได้เป็นแค่ช่องแสดงผล
