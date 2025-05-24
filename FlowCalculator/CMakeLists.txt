cmake_minimum_required(VERSION 3.30)
project(FlowCalculator)

set(CMAKE_CXX_STANDARD 20)

include(FetchContent)

FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/refs/heads/main.zip
)
FetchContent_MakeAvailable(googletest)

include_directories(${INCLUDE_DIR}/include)

file(GLOB_RECURSE APP_SOURCES CONFIGURE_DEPENDS src/*.cpp)
list(REMOVE_ITEM APP_SOURCES ${CMAKE_SOURCE_DIR}/src/main.cpp)

set(APP_MAIN src/main.cpp)

file(GLOB_RECURSE TEST_SOURCES CONFIGURE_DEPENDS tests/*.cpp)

add_library(AppLib ${APP_SOURCES})
target_include_directories(AppLib PUBLIC ${CMAKE_SOURCE_DIR}/include)

add_executable(FlowCalculator ${APP_MAIN})

target_link_libraries(FlowCalculator PRIVATE AppLib)

add_executable(Testy ${TEST_SOURCES})
target_link_libraries(Testy PRIVATE AppLib gtest_main)

target_include_directories(FlowCalculator PRIVATE ${CMAKE_SOURCE_DIR}/include)
target_include_directories(Testy PRIVATE ${CMAKE_SOURCE_DIR}/include)

enable_testing()
add_test(NAME AllTests COMMAND Testy)