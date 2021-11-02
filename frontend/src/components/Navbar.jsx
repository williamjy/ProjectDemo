import React, { useState, useContext } from "react";
import Toolbar from "@material-ui/core/Toolbar";
import SearchIcon from "@material-ui/icons/Search";
import InputBase from "@material-ui/core/InputBase";
import { makeStyles, createStyles } from "@material-ui/core/styles";
import { NavbarStyled } from "../styles/NavbarStyled";
import { useHistory } from "react-router";
import { StoreContext } from "../utils/store";
import { NavLink } from "../styles/NavLink";
import { Menu, MenuItem } from "@material-ui/core";
import AccountCircle from "@material-ui/icons/AccountCircle";
import { IconButtonShowSmall } from "../styles/IconButtonShowSmall";
import IconButton from "@material-ui/core/IconButton";
import logo from "../assets/valueEatsLogo.png";
import request from "../utils/request";

const useStyles = makeStyles((theme) =>
  createStyles({
    searchContainer: {
      // border: "1px solid #FF855B",
      borderRadius: 10,
      whiteSpace: "nowrap",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      paddingLeft: 10,
      color: "#FF855B",
    },
    logo: {
      maxWidth: "17%",
      "&:hover": {
        cursor: "pointer",
        color: "#e06543",
      },
    },
    barSize: {
      flex: 1,
    },
    searchBar: {
      fontSize: "1em",
      color: "#FF855B",
      width: "22vw",
      paddingRight: 10,
      backgroundColor: "transparent",
      borderBottom: "1px solid rgba(255, 132, 91, 0.5)",
      "&.Mui-focused": {
        borderBottom: "2px solid #FF855B !important",
      },
    },
    menu: {
      flex: 0,
    },
    singleLine: {
      whiteSpace: "nowrap",
      padding: "0px",
    },
  })
);

export default function Navbar () {
  const classes = useStyles();
  const history = useHistory();

  const context = useContext(StoreContext);
  const setAlertOptions = context.alert[1];
  const [auth, setAuth] = context.auth;
  const [isDiner, setIsDiner] = context.isDiner;
  const [openMenu, setOpenMenu] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const anchorElement = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const [search, setSearch] = useState(
    localStorage.getItem("searchTerm") ? localStorage.getItem("searchTerm") : ""
  );

  const handleLogout = async () => {
    console.log("You are getting logged out");
    const logoutResponse = await request.post("logout", {}, auth);
    const logoutData = await logoutResponse.json();
    if (logoutResponse.status === 200) {
      setAlertOptions({
        showAlert: true,
        variant: "success",
        message: logoutData.message,
      });
      setAuth(null);
      setIsDiner(null);
      localStorage.removeItem("token");
      localStorage.removeItem("isDiner");
      localStorage.setItem("searchTerm", "");
      history.push("/");
    } else {
      setAlertOptions({
        showAlert: true,
        variant: "error",
        message: logoutData.message,
      });
    }
  };

  const handleLogoClick = () => {
    console.log(localStorage.getItem("token"));
    clearSearch();
    if (auth === null) return history.push("/");
    // if token is manually deleted from localstorage on browser
    if (localStorage.getItem("token") === null) {
      setAuth(null);
      return history.push("/");
    }
    if (isDiner === "true") return history.push("/DinerLanding");
    if (isDiner === "false") return history.push("/EateryLanding");
  };

  const handleSearch = () => {
    // move to search page only if there is a search term
    if (search !== "") {
      localStorage.setItem("searchTerm", search);
      history.push({
        pathname: "/SearchResults",
        search: `?query=${search}`,
        state: { search: search },
      });
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      handleSearch();
    }
  };

  const clearSearch = () => {
    localStorage.setItem("searchTerm", "");
  };

  return (
    <NavbarStyled isDiner={isDiner} elevation={0}>
      <Toolbar className={classes.singleLine}>
        <img
          src={logo}
          alt="Value Eats logo"
          className={classes.logo}
          onClick={handleLogoClick}
        />
        {isDiner === "true" && (
          <Toolbar className={classes.barSize}>
            <div className={classes.searchContainer}>
              <div>
                <InputBase
                  onChange={(e) => setSearch(e.target.value)}
                  className={classes.searchBar}
                  placeholder="Restaurants, postcode, cuisines..."
                  inputProps={{
                    "aria-label": "search",
                  }}
                  value={search}
                  onKeyPress={handleKeyPress}
                />
              </div>
              <IconButton
                type="submit"
                aria-label="search"
                onClick={handleSearch}
                style={{ color: "#FF855B" }}
              >
                <SearchIcon />
              </IconButton>
            </div>
          </Toolbar>
        )}
        {
          // Hacky fix for flex stlying
          isDiner === "false" && <div style={{ flex: 1 }}></div>
        }
        <NavLink
          onClick={clearSearch}
          isDiner={isDiner}
          to={isDiner === "true" ? "/DinerLanding" : "/EateryLanding"}
        >
          HOME
        </NavLink>
        {isDiner === "false" && (
          <NavLink isDiner={isDiner} to="/RedeemVoucher">
            REDEEM
          </NavLink>
        )}
        <NavLink
          onClick={clearSearch}
          isDiner={isDiner}
          to={isDiner === "true" ? "/DinerProfile" : "/EateryProfile"}
        >
          PROFILE
        </NavLink>
        {isDiner === "true" && (
          <NavLink isDiner={isDiner} to="/DinerVouchers" onClick={clearSearch}>
            MY VOUCHERS
          </NavLink>
        )}
        {isDiner === "false" && (
          <NavLink isDiner={isDiner} to="/EditEateryProfile">
            EDIT PROFILE
          </NavLink>
        )}
        <NavLink isDiner={isDiner} onClick={handleLogout}>
          LOGOUT
        </NavLink>

        <IconButtonShowSmall
          isDiner={isDiner}
          onClick={(e) => {
            setOpenMenu(true);
            anchorElement(e);
          }}
          color="inherit"
        >
          <AccountCircle style={{ fontSize: "50px", color: "#FF855B" }} />
        </IconButtonShowSmall>
        <Menu
          className={classes.menu}
          id="simple-menu"
          anchorEl={anchorEl}
          keepMounted
          getContentAnchorEl={null}
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "left",
          }}
          open={openMenu}
          onClose={() => setOpenMenu(false)}
        >
          <MenuItem
            onClick={() =>
              isDiner
                ? history.push("/DinerLanding")
                : history.push("/EateryLanding")
            }
          >
            Home
          </MenuItem>
          <MenuItem
            onClick={() =>
              isDiner
                ? history.push("/DinerProfile")
                : history.push("/EateryProfile")
            }
          >
            Profile
          </MenuItem>
          {isDiner === "true" && (
            <MenuItem onClick={() => history.push("/DinerVouchers")}>
              My Vouchers
            </MenuItem>
          )}
          {isDiner === "false" && (
            <MenuItem onClick={() => history.push("/EditEateryProfile")}>
              Edit Profile
            </MenuItem>
          )}
          <MenuItem onClick={handleLogout}>Logout</MenuItem>
        </Menu>
      </Toolbar>
    </NavbarStyled>
  );
}
