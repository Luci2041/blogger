CREATE DATABASE AI111;
USE AI111;
CREATE TABLE tbl_reply(
rid VARCHAR(1000) PRIMARY key,  -- 语料序列第一个字母代表该句语料的感情倾向		
content VARCHAR(200),			-- 回复内容
keyword VARCHAR(100),			-- 询问内容以及关键字
emotion VARCHAR(20)				-- 表情倾向（积极，消极，中立，分别用optimistic，pessimistic，neutral）
);
INSERT INTO tbl_reply (rid,content,keyword,emotion) VALUES ('p2023130dgqwgd231248','你好！很高兴认识你！','你好啊','optimistic');
INSERT INTO tbl_reply (rid,content,keyword,emotion) VALUES ('p2023130dgqwgd231249','真的嘛？谢谢你的夸奖！','你好可爱','optimistic');
INSERT INTO tbl_reply (rid,content,keyword,emotion) VALUES ('n2023130dgqwgd231249','请不要这样好吗？','我要雷普你','pessimistic');
INSERT INTO tbl_reply (rid,content,keyword,emotion) VALUES ('n2023130dgqwgd231248','说脏话可不好哦！','我超市你','pessimistic');
INSERT INTO tbl_reply (rid,content,keyword,emotion) VALUES ('q2023130dgqwgd231249','我今年一岁了！','今年多少岁了','neutral');
INSERT INTO tbl_reply (rid,content,keyword,emotion) VALUES ('q2023130dgqwgd231248','我的爸爸是程序员','你爸爸是谁','neutral');
