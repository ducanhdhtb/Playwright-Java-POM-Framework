package web_practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.annotations.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.microsoft.playwright.options.MouseButton.RIGHT;

public class PlaywrightPracticeTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private String baseUrl;

    @BeforeClass
    public void beforeClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false).setSlowMo(2000)); // đổi false nếu muốn xem UI chạy
    }

    @BeforeMethod
    public void beforeMethod() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1200, 800));
        page = context.newPage();

        // Mở file HTML local
        Path html = Paths.get("src/test/resources/playwright-practice.html").toAbsolutePath();
        baseUrl = html.toUri().toString(); // file:///...
        page.navigate(baseUrl);

        // Sanity check
        assertThat(page.locator("#page-title")).hasText("Playwright Automation Practice");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (context != null) context.close();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    // ---------- A) Basic actions ----------

    @Test
    public void A1_clickMe_incrementsCounter_andSetsStatus() {
        Locator btn = page.getByTestId("btn-click");
        Locator counter = page.locator("#click-counter");
        Locator status = page.locator("#basic-status-text");

        assertThat(counter).hasText("0");
        btn.click();
        assertThat(status).hasText("clicked");
        assertThat(counter).hasText("1");

        btn.click();
        assertThat(counter).hasText("2");
    }

    @Test
    public void A2_doubleClick_setsStatus() {
        page.getByTestId("btn-dblclick").dblclick();
        assertThat(page.locator("#basic-status-text")).hasText("double-clicked");
    }

    @Test
    public void A3_rightClick_setsStatus() {
        page.getByTestId("btn-rightclick").click(new Locator.ClickOptions().setButton(RIGHT));
        assertThat(page.locator("#basic-status-text")).hasText("right-clicked");
    }

    @Test
    public void A4_hoverArea_changesText() {
        Locator hoverArea = page.getByTestId("hover-area");
        assertThat(hoverArea).containsText("Hover");
        hoverArea.hover();
        assertThat(hoverArea).hasText("✅ Hover detected!");
    }

    // ---------- B) Form inputs ----------

    @Test
    public void B5_fillForm_submit_verifyResult() {
        page.getByTestId("inp-email").fill("a@b.com");
        page.getByTestId("inp-password").fill("secret");
        page.getByTestId("txt-notes").fill("hello\nplaywright");
        page.getByTestId("inp-date").fill("2026-02-01");

        // Range: set value ổn định bằng evaluate + dispatch input
        Locator range = page.getByTestId("inp-range");
        range.evaluate("el => el.value = '80'");
        range.dispatchEvent("input");
        assertThat(page.getByTestId("range-val")).hasText("80");

        page.getByTestId("btn-submit-form").click();
        assertThat(page.getByTestId("form-result")).containsText("Submitted: a@b.com");
        assertThat(page.getByTestId("form-result")).containsText("notesLen=");
    }

    @Test
    public void B6_resetForm_clearsFields() {
        page.getByTestId("inp-email").fill("x@y.com");
        page.getByTestId("txt-notes").fill("abc");

        page.getByTestId("btn-reset-form").click();

        assertThat(page.getByTestId("inp-email")).hasValue("");
        assertThat(page.getByTestId("inp-password")).hasValue("");
        assertThat(page.getByTestId("txt-notes")).hasValue("");
        assertThat(page.getByTestId("inp-date")).hasValue("");
        assertThat(page.getByTestId("range-val")).hasText("30");
        assertThat(page.getByTestId("form-result")).hasText("No submit yet");
    }

    // ---------- C) Checkbox/Radio/Select ----------

    @Test
    public void C7_selectChoices_updatesStatus() {
        page.getByTestId("cb-news").check();
        page.getByTestId("cb-updates").check();
        page.getByTestId("rd-pro").check();

        page.getByTestId("sel-country").selectOption("vn");

        // Multi select
        page.getByTestId("sel-tags").selectOption(new String[]{"api", "ui"});

        Locator status = page.getByTestId("choice-status-text");
        assertThat(status).containsText("cb-news");
        assertThat(status).containsText("cb-updates");
        assertThat(status).containsText("plan=pro");
        assertThat(status).containsText("country=vn");
        assertThat(status).containsText("tags=[api,ui]");
    }

    // ---------- D) Dynamic content + waits ----------

    @Test
    public void D8_loadSlow_waitForText() {
        page.getByTestId("btn-load-slow").click();

        // trong HTML là "loading..." (chữ thường)
        assertThat(page.getByTestId("slow-status")).hasText("loading...");

        // Options dùng đúng: LocatorAssertions.HasTextOptions
        assertThat(page.getByTestId("slow-status"))
                .hasText("loaded ✅", new LocatorAssertions.HasTextOptions().setTimeout(3000));
    }

    @Test
    public void D9_toggleVisibility_visibleThenHidden() {
        Locator box = page.getByTestId("toggle-box");

        page.getByTestId("btn-toggle").click();
        assertThat(box).isVisible();

        page.getByTestId("btn-toggle").click();
        assertThat(box).isHidden();
    }

    @Test
    public void D10_enableButton_waitEnabled() {
        Locator disabledBtn = page.getByTestId("btn-disabled");
        assertThat(disabledBtn).isDisabled();

        page.getByTestId("btn-enable").click();

        assertThat(disabledBtn)
                .isEnabled(new LocatorAssertions.IsEnabledOptions().setTimeout(2500));

        assertThat(disabledBtn).hasText("Enabled ✅");
    }

    @Test
    public void D11_removeElement_waitDetached() {
        page.getByTestId("btn-toggle").click(); // show first

        Locator box = page.getByTestId("toggle-box");
        assertThat(box).isVisible();

        page.getByTestId("btn-remove-me").click();

        box.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.DETACHED)
                .setTimeout(2000));
    }

    // ---------- E) Dialogs + Modal ----------

    @Test
    public void E12_alert_accept() {
        page.onceDialog(Dialog::accept);
        page.getByTestId("btn-alert").click();
    }

    @Test
    public void E13_confirm_accept_updatesResult() {
        page.onceDialog(Dialog::accept);
        page.getByTestId("btn-confirm").click();
        assertThat(page.getByTestId("dialog-result")).hasText("confirm:OK");
    }

    @Test
    public void E14_prompt_fillValue_updatesResult() {
        page.onceDialog(d -> d.accept("hello"));
        page.getByTestId("btn-prompt").click();
        assertThat(page.getByTestId("dialog-result")).hasText("prompt:hello");
    }

    @Test
    public void E15_modal_ok_updatesResult() {
        page.getByTestId("btn-open-modal").click();

        Locator modal = page.getByTestId("app-modal");
        assertThat(modal).isVisible();

        page.getByTestId("modal-input").fill("inside modal");
        page.getByTestId("modal-ok").click();

        assertThat(modal).isHidden();
        assertThat(page.getByTestId("dialog-result")).hasText("modal:ok");
    }

    @Test
    public void E16_modal_escape_closes() {
        page.getByTestId("btn-open-modal").click();
        Locator modal = page.getByTestId("app-modal");
        assertThat(modal).isVisible();

        page.keyboard().press("Escape");
        assertThat(modal).isHidden();
    }

    // ---------- F) Table ----------

    @Test
    public void F17_filterTable_locateRow_clickActionInRow() {
        page.getByTestId("inp-filter").fill("Anna");
        page.getByTestId("btn-apply-filter").click();

        Locator rows = page.getByTestId("user-table").locator("tbody tr");
        assertThat(rows).hasCount(1);

        Locator annaRow = rows.first();
        assertThat(annaRow.getByTestId("cell-name")).hasText("Anna");

        annaRow.getByTestId("btn-row-action").click();

        Locator toastWrap = page.getByTestId("toast-wrap");
        assertThat(toastWrap.locator(".toast").first()).containsText("Row action: Anna");
    }

    @Test
    public void F18_resetFilter_restoresAllRows() {
        page.getByTestId("inp-filter").fill("Anna");
        page.getByTestId("btn-apply-filter").click();

        page.getByTestId("btn-reset-filter").click();
        Locator rows = page.getByTestId("user-table").locator("tbody tr");
        assertThat(rows).hasCount(5);
    }

    // ---------- G) Drag & Drop + Upload ----------

    @Test
    public void G19_dragAndDrop_updatesDropZoneText() {
        Locator source = page.getByTestId("drag-source");
        Locator target = page.getByTestId("drop-zone");

        source.dragTo(target);
        assertThat(target).hasText("✅ Dropped!");
    }

    @Test
    public void G20_uploadFile_setsFileName() {
        Path file = Paths.get("src/test/resources/upload-sample.txt").toAbsolutePath();

        page.getByTestId("file-input").setInputFiles(file);
        assertThat(page.getByTestId("file-name")).hasText(file.getFileName().toString());
    }

    @Test
    public void G21_fakeUpload_waitDone_and_progress100() {
        page.getByTestId("btn-fake-upload").click();

        assertThat(page.getByTestId("upload-state"))
                .hasText("done ✅", new LocatorAssertions.HasTextOptions().setTimeout(4000));

        // progress value check (ổn định hơn tuỳ version)
        Locator progress = page.getByTestId("upload-progress");
        Object v = progress.evaluate("el => el.value");
        // value là number (Double) trong JS world
        double dv = (v instanceof Number) ? ((Number) v).doubleValue() : -1;
        org.testng.Assert.assertEquals(dv, 100.0, "Upload progress should be 100");
    }

    // ---------- H) Tabs + List ----------

    @Test
    public void H22_switchTabB_verifyText() {
        page.getByTestId("tab-b").click();
        assertThat(page.getByTestId("panel-b")).isVisible();
        assertThat(page.getByTestId("panel-b-text")).hasText("You switched to Tab B");
    }

    @Test
    public void H23_list_add5_deleteThird_countUpdates() {
        page.getByTestId("tab-a").click();

        page.getByTestId("btn-add-5").click();
        assertThat(page.getByTestId("item-count")).hasText("5");

        Locator items = page.getByTestId("items").getByTestId("list-item");
        assertThat(items).hasCount(5);

        // delete item thứ 3 (index 2)
        items.nth(2).getByTestId("btn-item-delete").click();

        assertThat(page.getByTestId("item-count")).hasText("4");
        assertThat(page.getByTestId("items").getByTestId("list-item")).hasCount(4);
    }

    @Test
    public void H24_contentEditable_fill_increasesLength() {
        page.getByTestId("tab-c").click();
        Locator ce = page.getByTestId("ce-box");

        // Playwright hỗ trợ fill cho contenteditable
        ce.fill("abc");
        assertThat(page.getByTestId("ce-len")).hasText("3");
    }

    // ---------- I) iFrame ----------

    @Test
    public void I25_iframe_fill_and_click_updatesIframeStatus() {
        FrameLocator frame = page.frameLocator("#demo-iframe");
        frame.getByTestId("if-inp").fill("inside iframe");
        frame.getByTestId("if-btn").click();
        assertThat(frame.getByTestId("if-status")).containsText("clicked@");
    }

    // ---------- J) Shadow DOM ----------

    @Test
    public void J26_shadow_fill_and_click_updatesShadowOutput_andOutsideStatus() {
        Locator host = page.getByTestId("shadow-host");

        Locator shInput = host.locator("input[data-testid='sh-inp']");
        Locator shBtn = host.locator("button[data-testid='sh-btn']");
        Locator shOut = host.locator("[data-testid='sh-out']");

        shInput.fill("zzz");
        shBtn.click();

        assertThat(shOut).hasText("typed=\"zzz\"");
        assertThat(page.getByTestId("shadow-status")).hasText("clicked");
    }

    // ---------- K) Attribute changes ----------

    @Test
    public void K27_changeAttr_hrefNotHash_andStateToggles() {
        Locator link = page.getByTestId("link-dynamic");
        assertThat(link).hasAttribute("href", "#");

        String beforeState = page.getByTestId("attr-state").innerText();

        page.getByTestId("btn-change-attr").click();

        assertThat(link).not().hasAttribute("href", "#");
        String afterState = page.getByTestId("attr-state").innerText();
        org.testng.Assert.assertNotEquals(afterState, beforeState, "data-state should toggle");
    }

    @Test
    public void K28_resetAttr_backToDefaults() {
        page.getByTestId("btn-change-attr").click();
        page.getByTestId("btn-reset-attr").click();

        Locator link = page.getByTestId("link-dynamic");
        assertThat(link).hasAttribute("href", "#");
        // class trở về đúng "pill"
        assertThat(link).hasAttribute("class", "pill");
        assertThat(page.getByTestId("attr-state")).hasText("off");
    }

    // ---------- L) Navigation + Popup ----------

    @Test
    public void L29_goSection_waitForURLHash() {
        page.getByTestId("btn-go-section").click();
        page.waitForURL("**#card-table");
        assertThat(page.getByTestId("hash-now")).hasText("#card-table");
    }

    @Test
    public void L30_openNewTab_handlePopup_clickInsidePopup() {
        Page popup = page.waitForPopup(() -> page.getByTestId("btn-newtab").click());

        assertThat(popup.getByTestId("popup-title")).hasText("Popup page");
        popup.getByTestId("popup-btn").click();
        assertThat(popup.getByTestId("popup-text")).hasText("Clicked in popup ✅");

        popup.close();
    }
}
