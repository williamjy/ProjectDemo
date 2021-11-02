/// <reference types="cypress" />

// To run: yarn run cypress open
context("Test ValueEats - Register new user", () => {
  beforeEach(() => {
    cy.viewport(1000, 600);
    cy.visit("localhost:3000");
  });

  // Could reset this to ensure that the number will be unique
  // I decided to go with a random large number as it will do the job for our case
  const testNumber = Math.floor((Math.random() * 1000000000) + 1);

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

    cy.wait(3000);

    cy.get("button").then(el => {
      expect(el.text()).to.equal("");
    });

    cy.get("button").eq(2).click();

    cy.get("li").eq(3).click();

    cy.wait(2000);

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });
  });

  it("Login as that user that was registered", () => {
    const email = "test_user_integration" + testNumber + "@gmail.com";
    const password = "Password1";
    cy.get("input")
      .eq(0)
      .focus()
      .type(email);

    cy.get("input")
      .eq(1)
      .focus()
      .clear()
      .type(password);

    cy.get("button").eq(0).click();

    cy.wait(2000);

    // This should work this time
    cy.get("button").then(el => {
      expect(el.text()).to.contain("");
    });
  });

  it("Register as a new eatery and logout", () => {
    const email = "test_eatery_integration" + testNumber + "@gmail.com";
    const password = "Password1";
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
      .type(email);

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

    // Remove one of the svg options
    cy.get("svg")
      .eq(0)
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

    cy.get("a")
      .eq(4)
      .click();

    cy.wait(2000);

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });
  });

  it("Login as that eatery that was registered", () => {
    const email = "test_eatery_integration" + testNumber + "@gmail.com";
    const password = "Password1";
    cy.get("input")
      .eq(0)
      .focus()
      .type(email);

    cy.get("input")
      .eq(1)
      .focus()
      .type(password + "invalid");

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });

    // typing an invalid password and logging in, should not allow the user to login
    cy.get("button").click();

    cy.wait(2000);

    cy.get("button").then(el => {
      expect(el.text()).to.contain("Log in");
    });

    cy.get("button").eq(0).click();

    cy.get("input")
      .eq(1)
      .focus()
      .clear()
      .type(password);

    cy.get("button").eq(0).click();

    // This should work this time
    cy.get("button").then(el => {
      expect(el.text()).to.contain("");
    });
  });
});
