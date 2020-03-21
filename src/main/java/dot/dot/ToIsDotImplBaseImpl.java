package dot.dot;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import dot.dependency.dependency.IsDependency;
import dot.dependency.did.did.IsDid;
import dot.dependency.output.IsOutput;
import dot.osv.input.IsInput;
import dot.osv.osv.IsOsv;
import dot.osv.osv.NotOsv;
import dot.osv.osv.ToIsOsvGrpc;
import dot.state.state.IsState;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.text.WordUtils;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final public class ToIsDotImplBaseImpl extends ToIsDotGrpc.ToIsDotImplBase {

    private final ToIsOsvGrpc.ToIsOsvBlockingStub stub;
    private Stopwatch produce = SimonManager.getStopwatch(this.getClass()
            .getName());

    public ToIsDotImplBaseImpl() {

        stub = ToIsOsvGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsOsvGrpc.SERVICE_NAME)
                .usePlaintext()
                .build());

    }

    @Override
    public void produce(final NotDot request, final StreamObserver<IsDot> responseObserver) {
        final Split start = produce.start();
        try {

            final ByteString isDescriptorBytes = request.getIsInput()
                    .getIsDescriptorBytes();
            final ByteString isMessageBytes = request.getIsInput()
                    .getIsMessageBytes();

            final Message message = ((Message.Builder) Class
                    .forName(isDescriptorBytes.toStringUtf8())
                    .getMethod("newBuilder")
                    .invoke(new Object[]{})).mergeFrom(isMessageBytes)
                    .build();

            final List<IsDependency> dependencies = new ArrayList<>();

            processFieldsIntoDependencies(message,
                    dependencies);

            final List<osv.osv.IsOsv> osvStrings = new ArrayList<>();

            for (IsDependency dependency : dependencies) {

                final String hostClass = message.getClass()
                        .getName();

                final String hostFile = hostClass.substring(0,
                        hostClass.lastIndexOf("."));
                final String infantFileLastPart = dependency.getIsOutput()
                        .getIsDid()
                        .getIsOutput()
                        .getIsStringValue()
                        .replaceAll(" ",
                                "")
                        .toLowerCase();
                final String infantFile = hostFile.substring(0,
                        hostFile.lastIndexOf(".")) + "." + infantFileLastPart + "." + infantFileLastPart;

                final IsOsv produce = stub.produce(NotOsv.newBuilder()
                        .setIsInput(IsInput.newBuilder()
                                .setIsState(IsState.newBuilder()
                                        .setIsValueString(infantFile + "/Is " + WordUtils.capitalizeFully(dependency.getIsOutput()
                                                .getIsDid()
                                                .getIsOutput()
                                                .getIsStringValue()))
                                        .build())
                                .build())
                        .build());

                osvStrings.add(produce.getIsOutput()
                        .getIsOsv());
            }

            responseObserver.onNext(IsDot.newBuilder()
                    .setIsOutput(dot.output.IsOutput.newBuilder()
                            .addAllIsOsv(osvStrings)
                            .build())
                    .build());
            responseObserver.onCompleted();
        } catch (Throwable e) {
            responseObserver.onError(Status.fromThrowable(e)
                    .asException());
        }
        final Split stop = start.stop();
        System.out.println(produce);
    }

    private void processFieldsIntoDependencies(final Message message, final List<IsDependency> dependencies) {

        for (Map.Entry<Descriptors.FieldDescriptor, Object> kv : message.getAllFields()
                .entrySet()) {

            final Object field = kv.getValue();

            if (field instanceof Message) {
                processFieldsIntoDependencies((Message) field,
                        dependencies);
            } else {
                dependencies.add(IsDependency.newBuilder()
                        .setIsOutput(IsOutput.newBuilder()
                                .setIsDid(IsDid.newBuilder()
                                        .setIsOutput(dot.dependency.did.output.IsOutput.newBuilder()
                                                .setIsStringValue(field.toString())
                                                .build())
                                        .build())
                                .build())
                        .build());
            }
        }
    }
}
