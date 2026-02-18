Chạy file AuthSetup: Để nó tạo ra auth.json.

Chạy file ShoppingTest: Quan sát nó tự động vào trang Inventory.

Tạo lỗi giả: Bạn hãy thử sửa dòng hasText("Thank you for your order!") thành một chữ khác (ví dụ: "Order Failed") rồi chạy lại.

Mở Trace Viewer:

Sau khi test fail, tìm file trong thư mục traces/buyProductTest.zip.

Mở Terminal gõ: mvn exec:java -e '-Dexec.mainClass=com.microsoft.playwright.CLI' '-Dexec.args=show-trace traces/buyProductTest.zip'