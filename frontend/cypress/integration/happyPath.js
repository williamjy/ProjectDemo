/// <reference types="cypress" />

context("Test happy path - update user profile, create eatery voucher, book this voucher", () => {
  beforeEach(() => {
    cy.viewport(1000, 600);
    cy.visit("localhost:3000");
  });

  // Could reset this to ensure that the number will be unique
  // I decided to go with a random large number as it will do the job for our case
  const testNumber = Math.floor((Math.random() * 1000000000) + 1);
  // const testNumber = 841019049;

  it("Register as a new user and logout", () => {
    const email = "test_user_integration" + testNumber + "@gmail.com";
    const password = "Password1";
    const name = "John Doe";

    // Initial sign up
    // Make sure we reached the login page by default
    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });

    cy.get("a")
      .eq(0)
      .focus()
      .click();

    cy.get("input")
      .eq(1)
      .focus()
      .type(name);

    cy.get("input")
      .eq(2)
      .focus()
      .type(email);

    // Make password invalid - too short
    cy.get("input")
      .eq(3)
      .focus()
      .type("password")
      .blur();

    cy.get("button").click();

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Sign Up");
    });

    cy.get("input")
      .eq(3)
      .focus()
      .clear()
      .type(password)
      .blur();

    cy.get("input")
      .eq(4)
      .focus()
      .type("password2");

    // Cannot sign up - password do not match
    cy.get("button").click();

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Sign Up");
    });

    // Change password confirm to valid so the passwords do match
    cy.get("input")
      .eq(4)
      .focus()
      .clear()
      .type(password)
      .blur();

    cy.get("button").click();
    // We should now be logged in - therefore sign up button should no longer be there

    cy.wait(2000);

    cy.get("button").then(el => {
      expect(el.text()).to.equal("");
    });

    cy.get("button").eq(2).click();

    cy.get("li").eq(1).click();

    cy.wait(2000);

    // Go to the user profile page
    cy.get("button").eq(2).then(el => {
      expect(el.text()).to.contain("Edit Profile");
    });

    cy.get("button").eq(2).click();

    cy.get("button").eq(6).then(el => {
      expect(el.text()).to.contain("Save changes");
    });

    cy.get("input").eq(2)
      .focus()
      .clear()
      .type("NEW NAME")
      .blur();

    cy.get("button").eq(6).click();

    cy.wait(1500);

    // Updated profile should now be saved
    cy.get("button").eq(3).then(el => {
      expect(el.text()).to.contain("Edit Profile");
    });

    // Now log out and create an eatery
    cy.get("button").eq(2).click();

    cy.get("li").eq(3).click();

    cy.wait(2000);

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });

    const eateryEmail = "test_eatery_integration" + testNumber + "@gmail.com";
    const eateryName = "Test eatery";

    // Initial sign up
    // Make sure we reached the login page by default
    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });

    cy.get("a")
      .eq(1)
      .focus()
      .click();

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Register");
    });

    cy.get("input")
      .eq(0)
      .focus()
      .type(eateryName);

    cy.get("input")
      .eq(1)
      .focus()
      .type(eateryEmail);

    cy.get("input")
      .eq(2)
      .focus()
      .type(password);

    cy.get("input")
      .eq(3)
      .focus()
      .type(password);

    cy.get("input")
      .eq(4)
      .focus()
      .type("Valid address");

    // Add cuisines
    cy.get("input")
      .eq(5)
      .focus().click();

    cy.get("li")
      .eq(5)
      .click();

    cy.get("input")
      .eq(5)
      .focus().click();

    cy.get("li")
      .eq(10)
      .click();

    cy.get("button").then(el => {
      expect(el.text()).to.equal("Register");
    });

    cy.get("button")
      .eq(2)
      .click();
    // We should now be logged in - therefore sign up button should no longer be there

    cy.wait(2000);

    cy.get("button").eq(1).then(el => {
      expect(el.text()).to.not.equal("Register");
    });

    cy.wait(2000);

    // We are now on the eatery landing page, create a new voucher
    cy.get("button")
      .eq(3)
      .click();

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Create voucher");
    });

    cy.get("input").eq(2)
      .focus()
      .type(15);

    cy.get("input").eq(3)
      .focus()
      .type(10);

    cy.get("input").eq(4)
      .focus()
      .type("2022-08-02T10:30");

    cy.get("input").eq(5)
      .focus()
      .type("2022-08-03T10:30");

    // Create the new voucher
    cy.get("button")
      .eq(7)
      .click();

    cy.wait(2000);

    // Edit the voucher
    cy.get("button")
      .eq(3)
      .click();

    cy.get("button").eq(16).then(el => {
      expect(el.text()).to.contain("Save changes");
    });

    // Change the dicsount amount
    cy.get("input").eq(8)
      .focus()
      .clear()
      .type(20);

    // Save changes to the new voucher
    cy.get("button")
      .eq(15)
      .click();

    cy.wait(1000);
    // logout and relogin as user

    cy.get("a")
      .eq(4)
      .click();

    cy.wait(2000);

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });

    cy.get("input")
      .eq(0)
      .focus()
      .type(email);

    cy.get("input")
      .eq(1)
      .focus()
      .clear()
      .type(password);

    cy.get("button").eq(1).click();

    cy.wait(5000);

    // This should work this time
    cy.get("button").then(el => {
      expect(el.text()).to.contain("");
    });

    // Want to sort our list of eateries by new, so we can access the one we just made this happy path
    cy.get(".MuiSelect-root").eq(0).click();
    cy.get("li").eq(6).click();
    cy.wait(2000);

    cy.get(".MuiCardMedia-root").eq(0).click();

    cy.wait(2000);

    // Should now be on the test eatery profile
    cy.get("button").then(el => {
      expect(el.text()).to.contain("20%");
    });

    // Now try to book the voucher that we just made
    cy.get("button").eq(3).click();
    cy.get("button").eq(9).click();
    // Voucher should have been booked, all that's left to do is view the voucher in "My Vouchers"
    cy.wait(2000);
    cy.get("button").eq(9).click();
    cy.wait(2000);
    cy.get(".MuiBox-root").eq(0).then(el => {
      expect(el.text()).to.contain("My Vouchers");
    });
  });
});
