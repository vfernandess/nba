cmake_minimum_required(VERSION 3.22.1)
project(keys CXX)

set(BALLDONTLIE_API_KEY "" CACHE STRING "BallDontLie API key")
set(BALLDONTLIE_BASE_URL "https://api.balldontlie.io/v1/" CACHE STRING "BallDontLie base URL")

# Values are written into a generated header as C++ raw string literals, so quotes,
# backslashes, spaces, '$' and ';' in a key cannot break the compiler command line.
set(KEYS_CONFIG_DIR "${CMAKE_CURRENT_BINARY_DIR}/generated")
file(WRITE "${KEYS_CONFIG_DIR}/keys_config.h"
"#pragma once
#define API_KEY R\"keys_delim(${BALLDONTLIE_API_KEY})keys_delim\"
#define BASE_URL R\"keys_delim(${BALLDONTLIE_BASE_URL})keys_delim\"
")

add_library(keys SHARED keys.cpp)
target_include_directories(keys PRIVATE "${KEYS_CONFIG_DIR}")
