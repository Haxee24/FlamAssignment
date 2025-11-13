cmake_minimum_required(VERSION 3.10.2)
project("edgeprocessor")

set(CMAKE_CXX_STANDARD 17)

add_definitions(-DANDROID_STL=c++_shared)

# Path to OpenCV
set(OpenCV_DIR "${CMAKE_SOURCE_DIR}/opencv/native/sdk/native/jni")

find_package(OpenCV REQUIRED)

add_library(edgeprocessor SHARED
    edgeprocessor.cpp
)

target_include_directories(edgeprocessor PRIVATE
    ${OpenCV_INCLUDE_DIRS}
)

target_link_libraries(edgeprocessor
    ${OpenCV_LIBS}
    log
    jnigraphics
)
