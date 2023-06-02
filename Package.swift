// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "shared",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "shared",
            targets: ["shared"])
    ],
    dependencies: [],
    targets: [
        .binaryTarget(
            name: "shared",
            url: "https://github.com/KevinnZou/KMMDeployPlugin/blob/feature/test_spm/shared.xcframework.zip",
            checksum: "356e4498632ff3391474b58b164ce068bdc0f7d167ebd161e3348dd0a56d6cf6"
        )
    ]
)