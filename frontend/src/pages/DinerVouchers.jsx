import React, { useState, useEffect, useContext } from "react";
import NavBar from "../components/Navbar";
import { Box, Tabs, Tab } from "@material-ui/core";
import { Redirect } from "react-router";
import DinerVoucher from "../components/DinerVoucher";
import { VoucherContainer } from "../styles/VoucherContainer";
import { Subtitle } from "../styles/Subtitle";
import { StoreContext } from "../utils/store";
import { logUserOut } from "../utils/logoutHelper";
import { ButtonStyled } from "../styles/ButtonStyle";
import { useHistory } from "react-router-dom";
import Loading from "../components/Loading";
import { MainContainer } from "../styles/MainContainer";
import { PageTitle } from "../styles/PageTitle";
import request from "../utils/request";

export default function DinerVouchers () {
  const context = useContext(StoreContext);
  const [token, setAuth] = context.auth;
  const setIsDiner = context.isDiner[1];
  const [showHistory, setShowHistory] = useState(0);
  const [vouchers, setVouchers] = useState(null);
  const [loading, setLoading] = useState(false);
  const history = useHistory();
  const getVouchers = async () => {
    setLoading(true);
    const response = await request.get("diner/voucher", token);
    const responseData = await response.json();
    if (response.status === 200) {
      console.log(responseData.vouchers);
      setVouchers(responseData.vouchers);
    } else if (response.status === 401) {
      logUserOut(setAuth, setIsDiner);
    } else {
      console.log("cannot get vouchers");
    }
    setLoading(false);
  };

  useEffect(() => {
    getVouchers();
  }, [token]);

  if (token === null) return <Redirect to="/" />;

  // when show historical checkbox is clicked
  // const handleHistory = (event) => {
  //   setShowHistory(event.target.checked);
  // };

  const noVouchersMessage = () => {
    return (
      <Box
        display="flex"
        flexDirection="column"
        marginTop="10%"
        alignItems="center"
        height="70vh"
        pt={2}
      >
        <Subtitle style={{ color: "#96ae33" }}>
          No Vouchers Purchased Yet..
        </Subtitle>
        <ButtonStyled
          widthPercentage={40}
          onClick={() => history.push("/DinerLanding")}
        >
          Find restaurants
        </ButtonStyled>
      </Box>
    );
  };

  const getCurrentVouchers = () => {
    if (!vouchers) return;
    if (vouchers.length === 0) {
      return noVouchersMessage();
    }
    if (
      vouchers.reduce((total, voucher) => {
        return total + (!voucher.used && voucher.isActive ? 1 : 0);
      }, 0) === 0 &&
      showHistory === 0
    ) {
      return (
        <Box
          display="flex"
          flexDirection="column"
          marginTop="10%"
          alignItems="center"
          height="70vh"
          pt={2}
        >
          <Subtitle style={{ color: "#96ae33" }}>
            No Currently Active Vouchers
          </Subtitle>
          <ButtonStyled
            widthPercentage={40}
            onClick={() => history.push("/DinerLanding")}
          >
            Find restaurants
          </ButtonStyled>
        </Box>
      );
    }
    return vouchers.map((item, key) => {
      return (
        !item.used &&
        item.isActive && (
          <DinerVoucher
            duration={item.duration}
            code={item.code}
            date={item.date}
            discount={item.discount}
            eateryID={item.eateryId}
            eatingStyle={item.eatingStyle}
            endTime={item.endTime}
            isActive={item.isActive}
            isRedeemable={item.isRedeemable}
            startTime={item.startTime}
            eateryName={item.eateryName}
            used={item.used}
            key={key}
            handleRefresh={() => getVouchers()}
          />
        )
      );
    });
  };

  const getPastVouchers = () => {
    if (!vouchers) return;
    if (vouchers.length === 0) {
      return noVouchersMessage();
    }
    if (
      vouchers.reduce((total, voucher) => {
        return total + (voucher.used || !voucher.isActive ? 1 : 0);
      }, 0) === 0 &&
      showHistory === 1
    ) {
      return (
        <Box
          display="flex"
          flexDirection="column"
          marginTop="10%"
          alignItems="center"
          height="70vh"
          pt={2}
        >
          <Subtitle style={{ color: "#96ae33" }}>No Past Vouchers</Subtitle>
          <ButtonStyled
            widthPercentage={40}
            onClick={() => history.push("/DinerLanding")}
          >
            Find restaurants
          </ButtonStyled>
        </Box>
      );
    }
    return vouchers.map((item, key) => {
      return (
        (item.used || !item.isActive) && (
          <DinerVoucher
            code={item.code}
            date={item.date}
            discount={item.discount}
            eateryID={item.eateryId}
            eatingStyle={item.eatingStyle}
            endTime={item.endTime}
            isActive={item.isActive}
            isRedeemable={item.isRedeemable}
            startTime={item.startTime}
            eateryName={item.eateryName}
            used={item.used}
            key={key}
          />
        )
      );
    });
  };

  return (
    <>
      <NavBar isDiner={true} />
      <MainContainer>
        <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
        >
          <PageTitle>My Vouchers</PageTitle>
          <Box>
            <Tabs value={showHistory} aria-label="simple tabs example">
              <Tab label="Active" onClick={() => setShowHistory(0)} />
              <Tab label="Historical" onClick={() => setShowHistory(1)} />
            </Tabs>
          </Box>
          <VoucherContainer>
            {showHistory === 0 && getCurrentVouchers()}
            {showHistory === 1 && getPastVouchers()}
            <Loading isLoading={loading} />
          </VoucherContainer>
        </Box>
      </MainContainer>
    </>
  );
}
