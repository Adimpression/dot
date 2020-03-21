package dot.dot;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import dot.input.IsInput;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import z.z.IsZ;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ToIsDotImplBaseImplTest extends main.Test {

    private ToIsDotGrpc.ToIsDotBlockingStub stub;

    @Before
    public void setUp() {

        stub = ToIsDotGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsDotGrpc.SERVICE_NAME)
                .usePlaintext()
                .build())
                .withWaitForReady();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void produce() throws IOException {
        final IsZ build = IsZ.newBuilder()
                .build();

        stub.produce(NotDot.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsDescriptorBytes(ByteString.copyFrom("z.z.IsZ".getBytes()))
                        .setIsMessageBytes(build
                                .toByteString())
                        .build())
                .build())
                .getIsOutput()
                .getIsOsvList()
                .forEach(System.out::println);

        final Descriptors.Descriptor descriptorForType = IsZ.getDescriptor();

        final DescriptorProtos.DescriptorProto descriptorProto = descriptorForType.toProto();

        final Descriptors.FileDescriptor file = descriptorForType.getFile();

        final DescriptorProtos.FileDescriptorProto fileDescriptorProto = file.toProto();

        System.out.println(fileDescriptorProto.getSerializedSize());

        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        fileDescriptorProto.writeTo(output);

        output.close();

        System.out.println(output.toString());
    }
}