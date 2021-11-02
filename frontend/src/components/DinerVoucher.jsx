import React from "react";
import { Box, Grid } from "@material-ui/core";
import { useHistory } from "react-router-dom";
import Countdown from "react-countdown";
import { handleTimeNextDay } from "../utils/helpers";
import { Trapezium } from "../styles/Trapezium";
import { LinkStyled } from "../styles/LinkStyled";
import { Title } from "../styles/Title";

export default function DinerVoucher ({
  duration,
  code,
  date,
  discount,
  eateryID,
  eatingStyle,
  endTime,
  isActive,
  isRedeemable,
  startTime,
  eateryName,
  used,
  handleRefresh,
}) {
  const history = useHistory();

  const handleViewEateryClick = () => {
    history.push(`/EateryProfile/${eateryName}/${eateryID}`);
  };

  return (
    <Box
      display="flex"
      justifyContent="space-around"
      style={{ border: "2px dotted #FF845B" }}
      bgcolor={used || !isActive ? "#d6d6d6" : "#FFF9F7"}
      opacity="0.1"
      margin="20px"
    >
      <Trapezium
      >
        <h1 style={{
          paddingLeft: "15px",
          marginTop: "25px",
          zIndex: 1,
          color: "white",
          fontSize: "300%"
        }}>
            {discount}%
            {/* - {eatingStyle} */}
        </h1>
      </Trapezium>
      <Grid
        container
        direction="row"
        justifyContent="center"
        alignItems="center"
      >
        <Box
          item
          display="flex"
          flexDirection="column"
          justifyContent="center"
          xs={4}
          mx={12}
        >
          <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" textAlign="center">
            <LinkStyled onClick={handleViewEateryClick}
              style={{
                textDecoration: "underline",
                fontSize: "150%",
                margin: "5px 0px",
              }}
            >
              {eateryName}
            </LinkStyled>
            <h3 style={{
              margin: "0px"
            }}
            >
              {eatingStyle} deal
            </h3>
            {used && (
              <h3 style={{ margin: "5px 0px" }}>
                Redeemed on {date}
              </h3>
            )}
            {!isRedeemable && !used && (
              <h3 style={{ margin: "5px 0px" }}>
                Use on {date} between {startTime} -{handleTimeNextDay(endTime)}
              </h3>
            )}
            {isRedeemable && !used && (
              <h3 style={{ margin: "5px 0px" }}>
                Time remaining{" "}
                <Countdown
                  onComplete={handleRefresh}
                  date={Date.now() + duration - 1000}
                />
              </h3>
            )}
          </Box>
        </Box>
        <Box item display="flex" justifyContent="center" xs={3}>
          <Box
            display="flex"
            flexDirection="column"
            justifyContent="center"
          >
            {!used && isActive && (
              <Box display="flex"
              justifyContent="center"
              border="2px solid #FF845B"
              borderRadius="10%">
                <Title style={{
                  margin: "0px",
                  padding: "0px 10px"
                }}>
                  {code}
                </Title>
              </Box>
            )}
          </Box>
        </Box>
      </Grid>
    </Box>
  );
}
