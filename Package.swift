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
            url: "https://maven.pkg.github.com/ColaGom/sunflower-kmm/com/samples/apps/sunflower/shared/1.0.2/shared-1.0.2.zip",
            checksum: "46e6210d5a275334a047c766ed21294cd1594b65343fdb343ffa7ad4130ce781"
        )
    ]
)