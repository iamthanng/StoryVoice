-- ============================================================
-- StoryVoice - Script chèn dữ liệu mẫu
-- Chạy trong MySQL Workbench hoặc command line
-- Đảm bảo Spring Boot đã chạy 1 lần để tạo bảng trước
-- ============================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;



USE story_voice;

-- ========================
-- 1. Thể loại (Genres)
-- ========================
INSERT INTO genres (name) VALUES
('Tiên hiệp'),
('Võ hiệp'),
('Huyền huyễn'),
('Đô thị'),
('Ngôn tình'),
('Kinh dị'),
('Lịch sử'),
('Khoa học viễn tưởng')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ========================
-- 2. Tác giả (Authors)
-- ========================
INSERT INTO authors (name, bio) VALUES
('Ngã Cật Tây Hồng Thị', 'Tác giả nổi tiếng với bộ truyện Đấu Phá Thương Khung.'),
('Tiêu Thanh Tử', 'Tác giả trẻ chuyên viết truyện ngôn tình hiện đại.'),
('Phong Lưu Thư Đãi', 'Chuyên gia thể loại tiên hiệp, hệ thống thế giới sâu sắc.'),
('Nguyên Long', 'Tác giả Panlong nổi tiếng thế giới.')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ========================
-- 3. Truyện (Stories)
-- ========================
INSERT INTO stories (title, author_id, genre_id, cover_image, description, status, created_at) VALUES
(
  'Đấu Phá Thương Khung',
  (SELECT id FROM authors WHERE name = 'Ngã Cật Tây Hồng Thị'),
  (SELECT id FROM genres WHERE name = 'Tiên hiệp'),
  NULL,
  'Tại thế giới của Đấu Khí, Tiêu Viêm - một thiên tài bị tước đoạt hết tài năng, từng bước vươn lên đỉnh cao với sự giúp đỡ của một linh hồn bí ẩn ẩn trong chiếc nhẫn cổ...',
  'COMPLETED',
  NOW()
),
(
  'Tiên Nghịch',
  (SELECT id FROM authors WHERE name = 'Phong Lưu Thư Đãi'),
  (SELECT id FROM genres WHERE name = 'Tiên hiệp'),
  NULL,
  'Vương Lâm vô tình lấy được không gian túi trữ vật của người tu tiên, bắt đầu con đường tu tiên nghịch thiên của mình...',
  'COMPLETED',
  NOW()
),
(
  'Hắc Tâm Lão Bản Lại Dỗ Tôi',
  (SELECT id FROM authors WHERE name = 'Tiêu Thanh Tử'),
  (SELECT id FROM genres WHERE name = 'Ngôn tình'),
  NULL,
  'Cô gái trẻ vô tình trở thành thư ký riêng của vị CEO quyền lực, không ngờ lại che giấu nhiều bí mật...',
  'ONGOING',
  NOW()
),
(
  'Bàn Long',
  (SELECT id FROM authors WHERE name = 'Nguyên Long'),
  (SELECT id FROM genres WHERE name = 'Huyền huyễn'),
  NULL,
  'Lâm Lôi bị ném vào thế giới hỗn loạn nơi rồng trị vì. Từ một con người bình thường, hắn từng bước trở thành chiến thần mạnh nhất...',
  'COMPLETED',
  NOW()
),
(
  'Đô Thị Tối Cường Phế Tế',
  (SELECT id FROM authors WHERE name = 'Tiêu Thanh Tử'),
  (SELECT id FROM genres WHERE name = 'Đô thị'),
  NULL,
  'Một phế vật bị gia tộc ruồng bỏ trở về sau 3 năm, mang theo sức mạnh của cả thần linh lẫn ma quỷ...',
  'ONGOING',
  NOW()
);

-- ========================
-- 4. Chương (Chapters)
-- Mỗi truyện 3 chương với access_level khác nhau
-- ========================

-- Đấu Phá Thương Khung
INSERT INTO chapters (story_id, title, content, chapter_number, access_level, created_at) VALUES
(
  (SELECT id FROM stories WHERE title = 'Đấu Phá Thương Khung'),
  'Chương 1: Tiêu Viêm',
  'Bầu trời xanh thẳm, không một gợn mây. Mặt trời chiếu xuống mảnh đất rộng lớn, vạn vật ngập chìm trong ánh nắng ấm áp.\n\nTrên đất đại lục Đấu Khí, sức mạnh quyết định tất cả. Người mạnh được kính trọng, kẻ yếu chỉ có thể cúi đầu.\n\nTiêu Viêm, mười lăm tuổi, ngồi một mình trên tảng đá lớn bên bờ hồ, ánh mắt nhìn xa xăm. Ba năm trước, hắn vốn là thiên tài bậc nhất của Tiêu gia, nhưng giờ đây... chỉ còn là một phế vật.\n\n"Tiêu Viêm, hôm nay ta có thể dạy ngươi tu luyện thêm một chút," giọng nói khàn khàn vang lên trong đầu hắn.\n\n"Đó là lão già Dược Trần..." Tiêu Viêm khẽ mỉm cười.',
  1,
  'PUBLIC',
  NOW()
),
(
  (SELECT id FROM stories WHERE title = 'Đấu Phá Thương Khung'),
  'Chương 2: Linh Hồn Trong Nhẫn',
  'Đêm đó, Tiêu Viêm ngồi bất động trên tảng đá. Hắn cảm nhận luồng năng lượng nhỏ bé chảy qua cơ thể, dù yếu ớt nhưng vẫn là tiến bộ.\n\n"Lão già, ta hỏi thật đấy... ngươi rốt cuộc là ai?" Tiêu Viêm thì thầm trong tâm trí.\n\nMột tiếng cười trầm khàn vang lên: "Ta à? Ta là người đã sống hàng trăm năm, thấy qua không biết bao nhiêu kẻ thiên tài... Nhưng ngươi, có điều gì đó rất đặc biệt."\n\nTiêu Viêm ngẩng đầu nhìn bầu trời sao, trong lòng bắt đầu nhen nhóm một ngọn lửa nhỏ.\n\nMột ngày nào đó, ta nhất định sẽ lấy lại tất cả những gì đã mất!',
  2,
  'PUBLIC',
  NOW()
),
(
  (SELECT id FROM stories WHERE title = 'Đấu Phá Thương Khung'),
  'Chương 3: Đột Phá Đấu Đồ [Member]',
  'Sau một tháng miệt mài tu luyện dưới sự chỉ dẫn của Dược Trần, Tiêu Viêm cảm nhận rõ ràng sự thay đổi trong cơ thể. Đấu Khí trong kinh mạch đang ngày một sung mãn hơn.\n\nSáng sớm hôm đó, hắn quyết định bước vào thiền định toàn diện.\n\nTừng dòng Đấu Khí cuộn chảy, hội tụ tại Đan Điền. Bỗng nhiên, một tiếng nổ nhỏ vang lên trong tâm trí, và hắn cảm nhận rõ ràng mình vừa vượt qua ngưỡng giới hạn...\n\nĐấu Đồ đệ nhị tinh! Tiêu Viêm đã đột phá!',
  3,
  'MEMBER',
  NOW()
);

-- Tiên Nghịch
INSERT INTO chapters (story_id, title, content, chapter_number, access_level, created_at) VALUES
(
  (SELECT id FROM stories WHERE title = 'Tiên Nghịch'),
  'Chương 1: Không Gian Túi',
  'Vương Lâm, một học sinh bình thường tại thị trấn nhỏ, không ai ngờ rằng cuộc đời hắn sẽ thay đổi hoàn toàn chỉ vì một buổi sáng đi học bình thường.\n\nTrên con đường vắng, hắn tình cờ nhặt được một chiếc túi vải cũ kỹ. Trong túi... trống rỗng. Nhưng khi hắn đưa tay vào, một cảm giác kỳ lạ chạy dọc sống lưng.\n\n"Đây là... không gian bên trong?" Vương Lâm há hốc mồm khi nhận ra không gian khổng lồ ẩn giấu trong chiếc túi bé nhỏ.',
  1,
  'PUBLIC',
  NOW()
),
(
  (SELECT id FROM stories WHERE title = 'Tiên Nghịch'),
  'Chương 2: Bắt Đầu Tu Tiên',
  'Bên trong không gian túi, Vương Lâm phát hiện vô số thứ quý giá: đan dược, pháp bảo, và quan trọng nhất là... một cuốn công pháp tu tiên.\n\nHắn đọc ngấu nghiến. Thế giới này hóa ra không chỉ có khoa học. Phía sau màn đêm là cả một thế giới tu tiên huyền bí, nơi con người có thể bay lên trời, sống hàng nghìn năm.\n\nVà hắn, Vương Lâm, sẽ là người tiếp theo bước vào thế giới đó.',
  2,
  'PUBLIC',
  NOW()
),
(
  (SELECT id FROM stories WHERE title = 'Tiên Nghịch'),
  'Chương 3: Luyện Khí Tầng Một [VIP]',
  'Mười ngày sau khi tìm thấy không gian túi, Vương Lâm cuối cùng cũng mở được kinh mạch và bắt đầu thu hút linh khí trời đất.\n\nCảm giác đó kỳ diệu không thể tả được. Như thể toàn bộ vũ trụ đang chảy vào cơ thể hắn, mang theo sức sống và năng lượng vô tận.\n\nLuyện Khí tầng một - chỉ là bước đầu tiên trên con đường vạn dặm. Nhưng với Vương Lâm, đây là khoảnh khắc thay đổi tất cả.\n\n*Đây là chương VIP - cảm ơn sự ủng hộ của bạn đọc!*',
  3,
  'VIP',
  NOW()
);

-- Hắc Tâm Lão Bản
INSERT INTO chapters (story_id, title, content, chapter_number, access_level, created_at) VALUES
(
  (SELECT id FROM stories WHERE title = 'Hắc Tâm Lão Bản Lại Dỗ Tôi'),
  'Chương 1: Ngày Đầu Tiên',
  'Tôi tên là Lâm Tiểu Hy, hai mươi ba tuổi, vừa tốt nghiệp đại học loại giỏi và hôm nay là ngày đầu tiên đi làm.\n\nCông ty Thiên Lãng - một trong những tập đoàn lớn nhất thành phố. Tôi được nhận vào vị trí thư ký, và theo như lời HR thì... sếp trực tiếp của tôi là CEO tập đoàn.\n\nTôi đứng trước cánh cửa văn phòng tổng giám đốc, hít một hơi thật sâu rồi gõ cửa.\n\n"Vào."\n\nGiọng nói trầm và lạnh, như băng tan trong nước.',
  1,
  'PUBLIC',
  NOW()
),
(
  (SELECT id FROM stories WHERE title = 'Hắc Tâm Lão Bản Lại Dỗ Tôi'),
  'Chương 2: Vị CEO Khó Tính',
  'Mười phút sau khi bước vào văn phòng, tôi đã hiểu tại sao không ai muốn làm thư ký cho ông ta.\n\nMười yêu cầu trong mười phút. Cà phê phải đúng nhiệt độ 65 độ C. Tài liệu phải được sắp xếp theo thứ tự ngày giờ chính xác đến từng giây. Lịch họp phải được cập nhật mỗi ba mươi phút một lần.\n\n"Cô Lâm," giọng ông ta vang lên khi tôi đang loay hoay với đống file, "Cà phê của tôi."\n\nTôi quay lại, đặt tách cà phê lên bàn. Đôi mắt tối thẳm của ông ta nhìn chằm chằm vào tôi khiến tôi suýt run.',
  2,
  'MEMBER',
  NOW()
);

-- ========================
-- 5. Tài khoản Admin và Member mẫu
-- Mật khẩu đều là: Password123!
-- (BCrypt hash - bạn có thể đổi sau qua API đăng ký)
-- ========================
-- NOTE: Tốt hơn là dùng API /api/auth/register để tạo tài khoản
-- vì Spring Security sẽ tự hash password đúng cách.
-- Script này chỉ để tham khảo cấu trúc bảng.

-- ========================
-- Kiểm tra dữ liệu
-- ========================
SELECT 'Genres:' as info, COUNT(*) as total FROM genres;
SELECT 'Authors:' as info, COUNT(*) as total FROM authors;
SELECT 'Stories:' as info, COUNT(*) as total FROM stories;
SELECT 'Chapters:' as info, COUNT(*) as total FROM chapters;

SELECT s.title, a.name AS author, g.name AS genre, s.status
FROM stories s
LEFT JOIN authors a ON s.author_id = a.id
LEFT JOIN genres g ON s.genre_id = g.id;
