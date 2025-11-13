cmake_minimum_required(VERSION 3.18.1)

set(CMAKE_CXX_STANDARD 17)

add_library(
        edge_native
        SHARED
        native-lib.cpp
        processor.cpp
)

target_include_directories(edge_native PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

find_library(
        log-lib
        log
)

target_link_libraries(
        edge_native
        ${log-lib}
)
